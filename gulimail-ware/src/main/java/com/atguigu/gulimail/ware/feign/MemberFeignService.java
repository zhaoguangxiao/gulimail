package com.atguigu.gulimail.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gulimail-member")
public interface MemberFeignService {


    @RequestMapping("member/useraddress/info/{id}")
    public R getAddrInfo(@PathVariable("id") Long id);

}
