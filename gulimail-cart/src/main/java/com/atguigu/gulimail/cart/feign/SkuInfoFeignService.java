package com.atguigu.gulimail.cart.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "gulimail-product")
public interface SkuInfoFeignService {

    @GetMapping("product/skuinfo/info/{skuId}")
    public R getSkuInfoBySkuId(@PathVariable("skuId") Long skuId);


    @GetMapping("product/skusaleattrvalue/stringList/{skuId}")
    public List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);


    @GetMapping("product/skuinfo/{skuId}/price")
    public R getPriceBySkuId(@PathVariable("skuId") Long skuId);

}
