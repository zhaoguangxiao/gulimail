package com.atguigu.gulimail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 模板引擎 thymeleaf
 * 1引入 spring-boot-starter-thymeleaf
 * 2关闭了模板引擎缓存 cache: false
 * 3静态资源都放在了 static 文件夹下,就可以按照路径直接访问
 * 4页面放在了templates 文件夹下可以直接被访问
 * 5springboot访问项目时,默认找index.html页面
 * 6不重启服务器实时更新thymeleaf 页面
 */
@EnableFeignClients(basePackages = "com.atguigu.gulimail.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimail.product.dao")
@SpringBootApplication
public class GulimailProductApplicationMain10000 {
    public static void main(String[] args) {
        SpringApplication.run(GulimailProductApplicationMain10000.class, args);
    }
}
