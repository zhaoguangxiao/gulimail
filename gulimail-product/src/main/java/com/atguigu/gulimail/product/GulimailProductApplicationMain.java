package com.atguigu.gulimail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.atguigu.gulimail.product.dao")
@SpringBootApplication
public class GulimailProductApplicationMain {
    public static void main(String[] args) {
        SpringApplication.run(GulimailProductApplicationMain.class, args);
    }
}
