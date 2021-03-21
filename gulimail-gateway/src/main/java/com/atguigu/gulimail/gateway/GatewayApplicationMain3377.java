package com.atguigu.gulimail.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**  如果在网关处整合 sentinel 让请求还没发送给微服务就已经拦截了他
 *
 *  
 *
 */
@EnableDiscoveryClient //开启网关注册发现
@SpringBootApplication
public class GatewayApplicationMain3377 {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplicationMain3377.class, args);
    }
}
