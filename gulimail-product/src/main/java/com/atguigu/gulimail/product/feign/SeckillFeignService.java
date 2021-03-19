package com.atguigu.gulimail.product.feign;


import com.atguigu.common.utils.R;
import com.atguigu.gulimail.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimail-seckill",fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {

    @GetMapping(value = "/currentSeckillSkus")
    public R getCurrentSeckillSkus();


    @GetMapping(value = "/seckill/{skuId}")
    public R getSeckillBuSkuId(@PathVariable("skuId") Long skuId);

}
