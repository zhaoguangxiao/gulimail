package com.atguigu.gulimail.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 使用rabbitmq
 * 1), 引入了amqp 场景 @link RabbitAutoConfiguration 就会自动生效
 * 2),给容器自动配置了RabbitTemplate  AmqpAdmin组件 CachingConnectionFactory 连接工厂  RabbitMessagingTemplate
 */

@EnableFeignClients(basePackages = "com.atguigu.gulimail.order.feign")
@EnableRabbit //开启rabbitmq
@EnableDiscoveryClient //开启服务注册与发现
@SpringBootApplication
public class GuliMailOrderApplicationMain9000 {
    public static void main(String[] args) {
        SpringApplication.run(GuliMailOrderApplicationMain9000.class, args);
    }
}
