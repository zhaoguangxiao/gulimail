package com.atguigu.gulimail.product.vo;

import com.atguigu.gulimail.product.entity.SkuInfoEntity;
import lombok.Data;

/**
 * 保存商品到reids的详细信息
 */

@Data
public class SeckillSkuRedisDetailsTo {

    SeckillSkuEntityVo seckillSkuEntityVo; //活动基本信息
    SkuInfoEntity skuInfoEntityVo; //商品的基本信息

    private Long startTime; //当前商品秒杀开始时间
    private Long endTime;//当前商品秒杀结束时间
    private String randomCode; //商品秒杀随机码
}
