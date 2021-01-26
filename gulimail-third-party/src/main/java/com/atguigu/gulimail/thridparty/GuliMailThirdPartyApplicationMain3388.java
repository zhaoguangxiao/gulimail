package com.atguigu.gulimail.thridparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class GuliMailThirdPartyApplicationMain3388 {
    public static void main(String[] args) {
        SpringApplication.run(GuliMailThirdPartyApplicationMain3388.class,args);
    }
}
