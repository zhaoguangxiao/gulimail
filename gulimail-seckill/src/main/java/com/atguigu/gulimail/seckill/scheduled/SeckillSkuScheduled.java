package com.atguigu.gulimail.seckill.scheduled;


import com.atguigu.gulimail.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.atguigu.common.constant.SeckillRedisConstant.IDEMPOTENCY_REDIS_UPLOAD_LOCK;

/**
 * 秒杀商品的定时上架
 * 每天晚上3点,上架最近三天需要秒杀的商品
 * 当天 00:00:00 -23:59:59
 * 明天 00:00:00 -23:59:59
 * 后天 00:00:00 -23:59:59
 */
@Slf4j
@Service
public class SeckillSkuScheduled {


    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 需要进行幂等性处理
     */
    @Scheduled(cron = "*/30 * * * * ?")
    public void uploadSeckillSkuLatest3Day() {
        log.info("正在准备上架秒杀商品...");
        //1 重复上架无需处理
        //加入一个分布式锁 获取到锁的人 才可以执行  获取一个锁
        //分布式锁,锁的业务完成,状态已经更新完成,释放完锁,其它才能拿到最新的状态
        RLock lock = redissonClient.getLock(IDEMPOTENCY_REDIS_UPLOAD_LOCK);
        try {
            //加锁 锁的释放时间为10秒
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Day();
        } catch (Exception e) {
            log.error("定时任务执行上架商品出现异常 {}", e.getMessage());
        } finally {
            //进行解锁
            lock.unlock();
        }
    }

}
