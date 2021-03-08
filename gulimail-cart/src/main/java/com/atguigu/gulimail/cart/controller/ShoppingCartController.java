package com.atguigu.gulimail.cart.controller;

import com.atguigu.gulimail.cart.interceptor.ShoppingCartInterceptor;
import com.atguigu.gulimail.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ShoppingCartController {


    /**
     * jd 浏览器的cookie 有一个user-key 标识用户身份,一个月后过期
     * 如果第一次使用 jd 的购物车功能,都会临时生成一个user-key ,浏览器以后每次访问都会带上user-key
     * <p>
     * 登录: session有
     * 未登录: 按照 cookie带来的 user-key
     * 第一次: 如果没有临时用户,则需要帮忙创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String pageList() {
        UserInfoTo info = ShoppingCartInterceptor.userInfoToThreadLocal.get();
        log.info("当前使用 ThreadLocal 拿到的用户信息为 {}", info);
        return "cartList";
    }


    /**
     * 添加商品到 购物车页面
     *
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart() {
        return "success";
    }




}
