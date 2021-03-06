package com.atguigu.gulimail.cart;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableFeignClients(basePackages = "com.atguigu.gulimail.cart.feign")
@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimailShoppingCartApplicationMain30000 {
    public static void main(String[] args) {
        SpringApplication.run(GulimailShoppingCartApplicationMain30000.class, args);
    }
}
