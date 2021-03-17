package com.atguigu.gulimail.seckill.to;

import com.atguigu.gulimail.seckill.vo.SeckillSkuEntityVo;
import com.atguigu.gulimail.seckill.vo.SkuInfoEntityVo;
import lombok.Data;

/**
 * 保存商品到reids的详细信息
 */

@Data
public class SeckillSkuRedisDetailsTo {

    SeckillSkuEntityVo seckillSkuEntityVo; //活动基本信息
    SkuInfoEntityVo skuInfoEntityVo; //商品的基本信息

    private Long startTime; //当前商品秒杀开始时间
    private Long endTime;//当前商品秒杀结束时间
    private String randomCode; //商品秒杀随机码
}
