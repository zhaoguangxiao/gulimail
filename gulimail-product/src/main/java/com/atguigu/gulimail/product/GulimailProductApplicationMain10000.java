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
 *
 * 使用 seata事务管理流程
 *  1), 本地下载 seata-server zip解压,把修改 config.txt 与 nacos-conf.sh 配置文件参考 https://www.processon.com/mindmap/60078e7b1e08534bec2a5092
 *  2), 导入pom文件 主要有2个 1个是 spring-cloud-starter-alibaba-seata 与 seata-all
 *  3), 新建配置文件,seata代理对象管理本地数据库
 *  4), nacos 新建本地微服务的配置文件 {spring.application.name}-txt-group  (注意: 类型为 SEATA_GROUP 内容为txt 的 default)
 *  5), 在 templates 文件下面添加 file.conf 与 registry,conf 文件 并在 application.yml 添加 tx-service-group: ${spring.application.name}-fescar-service-group
 *
 *
 *
 *  使用sentinel 来保护feign 调用,熔断
 *  1), 调用方的熔断,feign.sentinel.enable=true
 *  2), 调用方手动指定远程服务的降级策略,远程服务被降级处理,触发我们的熔断回调方法
 *  3),超大浏览的时候,必须牺牲一些远程服务,在服务的提供方(远程服务)指定降级策略,提供方是在运行,但是不运行自己的业务逻辑,返回的是默认的数据(限流的数据)
 *
 *
 *
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
