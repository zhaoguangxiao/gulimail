package com.atguigu.gulimail.cart.properties;

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
