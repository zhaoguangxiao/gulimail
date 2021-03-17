package com.atguigu.gulimail.seckill.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 远程调用 优惠服务
 */
@FeignClient(value = "gulimail-coupon-7000")
public interface CouponFeignService {


    @GetMapping(value = "coupon/seckillsession/lastThreeDays")
    public R getLastThreeDays();

}
