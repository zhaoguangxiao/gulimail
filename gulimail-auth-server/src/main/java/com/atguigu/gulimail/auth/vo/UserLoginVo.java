package com.atguigu.gulimail.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginVo {


    @NotEmpty(message = "登录用户名不能为空")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
    private String loginUserName;

    @NotEmpty(message = "登录密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String loginPassword;

}
