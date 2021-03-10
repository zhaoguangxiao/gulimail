package com.atguigu.gulimail.order.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimail-member")
public interface UserAddressFeignService {

    @GetMapping("member/useraddress/{userId}")
    public R userAddressByUserId(@PathVariable("userId") Long userId);

}
