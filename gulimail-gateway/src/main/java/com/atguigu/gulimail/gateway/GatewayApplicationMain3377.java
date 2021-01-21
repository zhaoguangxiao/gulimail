package com.atguigu.gulimail.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient //开启网关注册发现
@SpringBootApplication
public class GatewayApplicationMain3377 {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplicationMain3377.class, args);
    }
}
