package com.atguigu.gulimail.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimail-product")
public interface SkuInfoService {


    @GetMapping(value = "/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
