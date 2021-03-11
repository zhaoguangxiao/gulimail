package com.atguigu.gulimail.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MyGulimailFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //当前上下文请求属性
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (null != attributes) {
                    //获取当前请求
                    HttpServletRequest request = attributes.getRequest();
                    if (null != request) {
                        //同步请求头属性 --header
                        String header = request.getHeader("Cookie");
                        //给新请求同步老请求的cookie
                        requestTemplate.header("Cookie", header);
                    }
                }
            }
        };
    }
}
