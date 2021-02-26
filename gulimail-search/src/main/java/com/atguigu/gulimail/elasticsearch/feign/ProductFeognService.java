package com.atguigu.gulimail.elasticsearch.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "gulimail-product")
public interface ProductFeognService {

    @GetMapping("product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);


    @GetMapping("product/brand/infos")
    public R brandIdSInfo(@RequestParam("brandIds") List<Long> brandIds);


    @GetMapping("product/category/info/{catId}")
    public R categoryInfo(@PathVariable("catId") Long catId);

}
