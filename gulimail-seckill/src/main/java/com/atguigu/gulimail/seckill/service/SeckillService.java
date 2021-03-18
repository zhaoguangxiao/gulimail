package com.atguigu.gulimail.seckill.service;

import com.atguigu.gulimail.seckill.to.SeckillSkuRedisDetailsTo;
import com.atguigu.gulimail.seckill.vo.RequestSeckillVo;

import java.util.List;

public interface SeckillService {

    /**
     * 把最近三天的商品上传redis
     */
    void uploadSeckillSkuLatest3Day();

    /**
     * 从redis 获取当前可以参与秒杀的商品信息
     */
    List<SeckillSkuRedisDetailsTo> getCurrentSeckillSkus();

    /** 根据skuid 从redis 获取活动详情
     * @param skuId
     * @return
     */
    SeckillSkuRedisDetailsTo getSeckillBuSkuId(Long skuId);

    /** 立即秒杀
     * @param requestSeckillVo
     * @return
     */
    String checkSeckill(RequestSeckillVo requestSeckillVo) throws InterruptedException;
}
