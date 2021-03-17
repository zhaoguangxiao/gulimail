package com.atguigu.gulimail.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 具体配置参考官网 https://github.com/redisson/redisson/wiki/1
 *
 * @date 2021年2月22日16:06:49
 */
@Configuration
public class MyRedissonConfig {

    /**
     * shutdown 服务停止会进行销毁
     * useSingleServer
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws IOException {
        //1 创建配置
        Config config = new Config();
        //2 根据config 对象创建 redisson 实例
        config.useSingleServer()
                .setAddress("redis://192.168.247.111:6379");
        //3 返回redisson 实例
        return Redisson.create(config);
    }

}
