package com.atguigu.gulimail.auth.feign;


import com.atguigu.common.utils.R;
import com.atguigu.gulimail.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "gulimail-member")
public interface UserFeignService {

    @PostMapping("member/user/user/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo);


}
