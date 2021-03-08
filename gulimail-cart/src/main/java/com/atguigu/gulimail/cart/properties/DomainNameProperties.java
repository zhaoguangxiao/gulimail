package com.atguigu.gulimail.cart.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "domain.name")
public class DomainNameProperties {

    private String domainName;  //设置cookie的顶级域名
    private String cookieName;  //设置缓存cookie名称

}
