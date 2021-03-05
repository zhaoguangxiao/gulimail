package com.atguigu.gulimail.auth.feign;


import com.atguigu.common.utils.R;
import com.atguigu.common.vo.GithubEntityVo;
import com.atguigu.gulimail.auth.vo.UserLoginVo;
import com.atguigu.gulimail.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "gulimail-member")
public interface UserFeignService {

    @PostMapping("member/user/user/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo);


    @PostMapping("member/user/user/login")
    public R login(@RequestBody UserLoginVo userLoginVo);


    @PostMapping("member/user/oauth/github")
    public R githubLogin(@RequestBody GithubEntityVo githubEntityVo);

}
