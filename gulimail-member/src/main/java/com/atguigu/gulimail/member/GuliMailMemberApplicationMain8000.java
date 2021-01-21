package com.atguigu.gulimail.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.atguigu.gulimail.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMailMemberApplicationMain8000 {
    public static void main(String[] args) {
        SpringApplication.run(GuliMailMemberApplicationMain8000.class, args);
    }
}
