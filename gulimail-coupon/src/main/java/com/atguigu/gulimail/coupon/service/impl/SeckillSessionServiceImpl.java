package com.atguigu.gulimail.coupon.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.atguigu.gulimail.coupon.service.SeckillSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.coupon.dao.SeckillSessionDao;
import com.atguigu.gulimail.coupon.entity.SeckillSessionEntity;
import com.atguigu.gulimail.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {


    @Autowired
    private SeckillSkuService seckillSkuService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLastThreeDays() {
        List<SeckillSessionEntity> seckillSessionEntities = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", getStartTime(), getEndThereDaysTime()));
        if (!CollectionUtils.isEmpty(seckillSessionEntities)) {
            return seckillSessionEntities.stream().map(item -> {
                item.setSeckillSkuEntities(seckillSkuService.listEntityByPromotionSessionId(item.getId()));
                return item;
            }).collect(Collectors.toList());
        }
        return null;
    }


    /**
     * @return 返回当前起始时间
     */
    private String getStartTime() {
        //1 获取当前时间
        LocalDate now = LocalDate.now(); //2021-3-17
        //2 获取最小的时间
        LocalTime min = LocalTime.MIN;
        //3 进行组合
        LocalDateTime result = LocalDateTime.of(now, min);//2021-3-17 00:00:00
        return DateUtil.format(result, DatePattern.NORM_DATETIME_PATTERN); //格式为 yyyy-MM-dd HH:mm:ss
    }


    /**
     * @return 返回当前3天后的结束时间
     */
    private String getEndThereDaysTime() {
        //1 获取当前时间
        LocalDate now = LocalDate.now(); //2021-3-17
        //2 获取三天后的时间
        LocalDate endTime = now.plusDays(2);//2021-3-20
        //2 获取最小的时间
        LocalTime max = LocalTime.MAX;
        //3 进行组合
        LocalDateTime result = LocalDateTime.of(endTime, max);//2021-3-20  23:59:59.999999999
        return DateUtil.format(result, DatePattern.NORM_DATETIME_PATTERN); //格式为 yyyy-MM-dd HH:mm:ss
    }
}