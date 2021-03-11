package com.atguigu.gulimail.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "gulimail-ware")
public interface WareFeignService {


    @PostMapping(value = "ware/waresku/hashStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

}
