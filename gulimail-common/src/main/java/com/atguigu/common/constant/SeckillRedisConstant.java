package com.atguigu.common.constant;

/**
 * 秒杀redis 常量
 */
public class SeckillRedisConstant {


    /**
     * SECKILL_REDIS_KEY+开始时间_结束时间 进行保存 活动的skuid 信息
     */
    public static final String SECKILL_REDIS_KEY = "seckill:sessions:";

    /**
     * SECKILL_REDIS_SKU_KEY 保存key 为skuid 值为 商品的详细信息(秒杀开始时间+结束时间+随机令牌+当前sku的详细信息)
     */
    public static final String SECKILL_REDIS_SKU_KEY = "seckill:skus";

    /**
     * SECKILL_SKU_STOCK_SEAPHORE_KEY+uuid 保存的是信号量的值
     */
    public static final String SECKILL_SKU_STOCK_SEAPHORE_KEY = "seckill:stock:";


    /**
     * IDEMPOTENCY_REDIS_UPLOAD_LOCK 用来解决商品上架的幂等性问题
     */
    public static final String IDEMPOTENCY_REDIS_UPLOAD_LOCK = "seckill:upload:lock";
}
