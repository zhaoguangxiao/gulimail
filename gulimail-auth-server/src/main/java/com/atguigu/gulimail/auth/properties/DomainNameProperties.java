package com.atguigu.gulimail.auth.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "domain.name")
public class DomainNameProperties {

    private String rootUrl; //主域名  http://gulimail.com
    private String sonLoginUrl; // http://auth.gulimail.com/login.html
    private String sonRegisterUrl; //http://auth.gulimail.com/register.html
    private String domainName;  //设置cookie的顶级域名
    private String cookieName;  //设置缓存cookie名称

}
