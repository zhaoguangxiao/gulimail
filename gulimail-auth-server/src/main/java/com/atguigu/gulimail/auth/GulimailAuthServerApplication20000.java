package com.atguigu.gulimail.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * redisHttpSession 核心原理
 * 1),导入了 RedisHttpSessionConfiguration
 * 1-1), 给容器中添加了 sessionRepository 组件--redis 操作session,session 增删改查封装类
 * 1-2),RedisHttpSessionConfiguration extends SpringHttpSessionConfiguration 给容器添加了springSessionRepositoryFilter session存储器,每个请求过来都必须经过filter
 * 2),springsession 核心原理是一个SessionRepositoryFilter ->doFilterInternal
 * ```
 * SessionRepositoryFilter<S>.SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryFilter.SessionRepositoryRequestWrapper(request, response);--包装原生请求对象,装饰者模式
 * SessionRepositoryFilter.SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryFilter.SessionRepositoryResponseWrapper(wrappedRequest, response);--包装原始响应对象
 * filterChain.doFilter(wrappedRequest, wrappedResponse);---将包装后的对象应用到了后面的整个执行链
 * ```
 * 包装这两个,有什么用?
 * 获取session的代码 HttpSession session=HttpServletSession.getSession(); 获取session的代码
 * wrappedRequest.getSession(); 重写了HeetServletSession的 getSession 这样每次你获取session 都会走装饰者模式自己写的获取session方法
 * <p>
 * springSession 会自动延期,浏览器一关,redis是有过期时间自动删除
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.atguigu.gulimail.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimailAuthServerApplication20000 {
    public static void main(String[] args) {
        SpringApplication.run(GulimailAuthServerApplication20000.class, args);
    }
}
