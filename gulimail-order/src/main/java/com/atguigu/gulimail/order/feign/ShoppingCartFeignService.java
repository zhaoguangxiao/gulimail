package com.atguigu.gulimail.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "gulimail-cart")
public interface ShoppingCartFeignService {

    @GetMapping("/currentUserCartItems")
    public R currentUserCartItems();

}
