package com.atguigu.gulimail.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gulimail-coupon-7000")
public interface CouponService {


    @RequestMapping(value = "coupon/coupon/member/list")
    public R memberCoupon();


}
