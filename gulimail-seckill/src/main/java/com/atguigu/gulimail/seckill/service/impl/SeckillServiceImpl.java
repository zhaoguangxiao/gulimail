package com.atguigu.gulimail.seckill.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.seckill.feign.CouponFeignService;
import com.atguigu.gulimail.seckill.feign.ProductFeignService;
import com.atguigu.gulimail.seckill.service.SeckillService;
import com.atguigu.gulimail.seckill.to.SeckillSkuRedisDetailsTo;
import com.atguigu.gulimail.seckill.vo.SeckillSessionEntityVo;
import com.atguigu.gulimail.seckill.vo.SkuInfoEntityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.SeckillRedisConstant.*;


@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public void uploadSeckillSkuLatest3Day() {
        //1 去数据库扫描参与活动的商品
        R lastThreeDays = couponFeignService.getLastThreeDays();
        log.info("远程查询当前三天的活动商品集合 {}", lastThreeDays.get("data"));
        List<SeckillSessionEntityVo> lastThreeDaysData = lastThreeDays.getData(new TypeReference<List<SeckillSessionEntityVo>>() {
        });

        if (!CollectionUtils.isEmpty(lastThreeDaysData)) {
            //缓存数据到 reids
            for (SeckillSessionEntityVo lastThreeDaysDatum : lastThreeDaysData) {
                //1 缓存活动信息
                saveRedisSessionInfos(lastThreeDaysDatum);
                //2 缓存活动的关联商品信息
                saveRedisSkuInfos(lastThreeDaysDatum);
            }

        }

    }

    private void saveRedisSkuInfos(SeckillSessionEntityVo lastThreeDaysDatum) {
        //准备hash 操作
        BoundHashOperations<String, String, Object> ops = stringRedisTemplate.boundHashOps(SECKILL_REDIS_SKU_KEY);
        lastThreeDaysDatum.getSeckillSkuEntities().stream().forEach(item -> {
            Boolean hasKey = ops.hasKey(item.getPromotionSessionId() + "_" + item.getSkuId().toString());
            if (!hasKey) {
                //缓存商品
                SeckillSkuRedisDetailsTo detailsTo = new SeckillSkuRedisDetailsTo();
                //设置秒杀信息
                detailsTo.setSeckillSkuEntityVo(item);
                //设置商品基本信息
                R info = productFeignService.info(item.getSkuId());
                log.info("远程调用商品服务,查询商品sku信息保存进 redis {} 如果是0表示查询成功", info.get("code"));
                SkuInfoEntityVo skuInfo = JSON.parseObject(JSON.toJSONString(info.get("skuInfo")), new TypeReference<SkuInfoEntityVo>() {
                });
                detailsTo.setSkuInfoEntityVo(skuInfo);
                //设置当前商品秒杀的时间信息
                detailsTo.setStartTime(lastThreeDaysDatum.getStartTime().getTime());
                detailsTo.setEndTime(lastThreeDaysDatum.getEndTime().getTime());
                //设置商品的随机码--为什么要引入随机码 ,如果不设置随机码只有在商品放出来之后才会放出来,不能直接通过url 直接访问
                String randomCode = IdUtil.simpleUUID();
                detailsTo.setRandomCode(randomCode);
                ops.put(item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString(), JSON.toJSONString(detailsTo));

                //如果当前商品的库存信息已经上架了 就不需要上架了


                //使用库存作为分布式信号量 --限流
                RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_SKU_STOCK_SEAPHORE_KEY + randomCode);
                //设置信号量量 --商品秒杀件数 作为信号量
                semaphore.trySetPermits(item.getSeckillCount().intValue());

            }
        });
    }

    private void saveRedisSessionInfos(SeckillSessionEntityVo lastThreeDaysDatum) {
        Long startTime = lastThreeDaysDatum.getStartTime().getTime();
        Long endTime = lastThreeDaysDatum.getEndTime().getTime();
        String redisKey = SECKILL_REDIS_KEY + startTime + "_" + endTime;
        //判断当前key 是否存在
        Boolean hasKey = stringRedisTemplate.hasKey(redisKey);
        if (!hasKey) {
            List<String> redisVal = lastThreeDaysDatum.getSeckillSkuEntities().stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
            //缓存活动信息
            stringRedisTemplate.opsForList().leftPushAll(redisKey, redisVal);
        }
    }


    @Override
    public List<SeckillSkuRedisDetailsTo> getCurrentSeckillSkus() {
        //1 确认当前时间属于那个秒杀场次
        long time = new Date().getTime();
        //1-2 获取redis 所有的秒杀场次信息
        Set<String> keys = stringRedisTemplate.keys(SECKILL_REDIS_KEY + "*");
        //redis 存储的key 为 seckill:sessions:1616025600000_1616032800000
        for (String key : keys) {
            //截取出时间区间 1616025600000_1616032800000
            String timeSection = key.replace(SECKILL_REDIS_KEY, "");
            //使用_进行分割
            String[] strings = timeSection.split("_");
            //获取到开始时间
            Long startTime = Long.parseLong(strings[0]);
            //获取到结束时间
            Long endTime = Long.parseLong(strings[1]);
            if (time >= startTime && time <= endTime) {
                //2 获取这个秒杀场次所有的商品信息
                List<String> list = stringRedisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, Object> hashOps = stringRedisTemplate.boundHashOps(SECKILL_REDIS_SKU_KEY);
                List<Object> multiGet = hashOps.multiGet(list);
                if (!CollectionUtils.isEmpty(multiGet)) {
                    return multiGet.stream().map(item -> {
                        SeckillSkuRedisDetailsTo redisDetailsTo = JSON.parseObject(item.toString(), new TypeReference<SeckillSkuRedisDetailsTo>() {
                        });
                        //注意随机码 不能直接传到前端页面 由于现在在秒杀时间段 所以这里没有设置为null
                        //redisDetailsTo.setRandomCode(null);
                        return redisDetailsTo;
                    }).collect(Collectors.toList());
                }
                break; //跳出循环
            }
        }


        return null;
    }
}
