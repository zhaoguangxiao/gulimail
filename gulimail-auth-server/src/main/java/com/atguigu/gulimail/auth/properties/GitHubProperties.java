package com.atguigu.gulimail.auth.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "github.configure")
public class GitHubProperties {

    public static final String ROOT_PATH_URL = "https://api.github.com/user";
    public static final String GET_GITHUB_USER_HEADER_KEY = "Authorization";
    public static final String GET_GITHUB_USER_HEADER_VAL = "token ";
    //获取token的url
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token?client_id=CLIENTID&client_secret=CLIENTSECRET&code=CODE&redirect_uri=REDIRECTURI";

    private String clientId;
    private String clientSecret;
    private String callback;

}
