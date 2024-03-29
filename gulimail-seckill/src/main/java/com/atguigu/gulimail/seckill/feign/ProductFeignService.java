package com.atguigu.gulimail.seckill.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimail-product")
public interface ProductFeignService {


    @GetMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);


}
