package com.atguigu.gulimail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients(basePackages = "com.atguigu.gulimail.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimail.product.dao")
@SpringBootApplication
public class GulimailProductApplicationMain10000 {
    public static void main(String[] args) {
        SpringApplication.run(GulimailProductApplicationMain10000.class, args);
    }
}
