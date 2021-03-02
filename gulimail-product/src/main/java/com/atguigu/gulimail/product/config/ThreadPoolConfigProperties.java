package com.atguigu.gulimail.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gulimail.thread")
public class ThreadPoolConfigProperties {


    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;


}
