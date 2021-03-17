package com.atguigu.gulimail.product.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "gulimail-seckill")
public interface SeckillFeignService {

    @GetMapping(value = "/currentSeckillSkus")
    public R getCurrentSeckillSkus();

}
