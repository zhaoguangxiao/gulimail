package com.atguigu.gulimail.product.feign;

import com.atguigu.common.to.SkuBoundsTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gulimail-coupon-7000")
public interface SkuBoundsFeignService {

    @PostMapping("/coupon/skubounds/save")
    R saveSkuBounds(@RequestBody SkuBoundsTo skuBoundsTo);


    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo reductionTo);
}
