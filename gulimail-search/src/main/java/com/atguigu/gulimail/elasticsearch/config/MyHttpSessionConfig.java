package com.atguigu.gulimail.elasticsearch.config;


import com.atguigu.gulimail.elasticsearch.properties.DomainNameProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class MyHttpSessionConfig {


    @Bean
    public CookieSerializer cookieSerializer(DomainNameProperties domainNameProperties) {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName(domainNameProperties.getDomainName());
        cookieSerializer.setCookieName(domainNameProperties.getCookieName());
        return cookieSerializer;
    }


    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }


}
