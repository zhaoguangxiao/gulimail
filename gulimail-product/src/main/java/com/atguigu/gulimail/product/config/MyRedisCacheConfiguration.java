package com.atguigu.gulimail.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 自定义 redis 配置类
 */
@EnableConfigurationProperties({CacheProperties.class})
@EnableCaching  //开启缓存注解
@Configuration
public class MyRedisCacheConfiguration {


    /**
     * 也可以直接向  public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties)  传入
     */
    @Autowired
    private CacheProperties cacheProperties;


    /** 配置文件 time-to-live 失效
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())); //修改key序列化
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));//修改value序列化

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        //将配置文件所有配置都生效
        if (redisProperties.getTimeToLive() != null) {
            configuration = configuration.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            configuration = configuration.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }

        if (!redisProperties.isCacheNullValues()) {
            configuration = configuration.disableCachingNullValues();
        }

        if (!redisProperties.isUseKeyPrefix()) {
            configuration = configuration.disableKeyPrefix();
        }
        return configuration;
    }


}
