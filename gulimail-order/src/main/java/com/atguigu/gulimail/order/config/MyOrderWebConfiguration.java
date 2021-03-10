package com.atguigu.gulimail.order.config;

import com.atguigu.gulimail.order.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MyOrderWebConfiguration implements WebMvcConfigurer {


    @Autowired
    private LoginUserInterceptor loginUserInterceptor;


    /**
     * 给容器添加一个拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
