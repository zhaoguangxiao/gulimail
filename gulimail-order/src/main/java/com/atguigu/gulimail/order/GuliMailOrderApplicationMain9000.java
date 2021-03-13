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
 *
 *
 *
 * 本地事务失效 @Transactional [同一个对象,同一个类中a方法调用b,c方法其中b,c方法写了Transactional 无法生效]
 * 原因: 同一个事务内方法互调默认是失效的,原因绕过了代理对象,事务使用代理对象来控制的
 * 解决方案: --使用代理对象来调用事务方法
 * 1),引入aop模块-spring-boot-starter-aop [核心 aspectj]
 * 2),@EnableAspectjAutoProxy 开启aspectj动态代理功能,以后所有的动态代理都是aspectj创建的,即使没有借口也可以创建动态代理
 * 3),设置 exposeProxy=true ,对外暴露代理对象
 * 4),用代理对象类互调 AopContext.currentProxy()转为指定的类型 ---来调用b,c方法,我们的Transactional 才会生效
 *
 *
 *  seata控制分布式事务
 *   1),每个微服务数据保存 un_log
 *   2),安装事务控制器 https://github.com/seata/seata/releases
 *   3),导入依赖 spring-cloud-starter-alibaba-seata
 *   4),解压并启动 seata-server  注册中心配置
 *   5),所有想要用到分布式事务的微服务使用 seata DruidDataSource 代理数据源
 *
 *
 *
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
