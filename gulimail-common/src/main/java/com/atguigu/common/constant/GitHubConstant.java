package com.atguigu.common.constant;

public class GitHubConstant {


    public static final String ROOT_PATH_URL = "https://api.github.com/user";

    public static final String GET_GITHUB_USER_HEADER_KEY = "Authorization";
    public static final String GET_GITHUB_USER_HEADER_VAL = "token ";

    // 这里填写在GitHub上注册应用时候获得 CLIENT ID
    public static final String CLIENT_ID = "7a5fefb44152bc4299e7";
    //这里填写在GitHub上注册应用时候获得 CLIENT_SECRET
    public static final String CLIENT_SECRET = "0fc96714c439204c3327218493c2aca1d8c1cf03";
    // 回调路径
    public static final String CALLBACK = "http://auth.gulimail.com/github/callback";

    //获取code的url
    public static final String CODE_URL = "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID + "&state=STATE&redirect_uri=" + CALLBACK + "";
    //获取token的url
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&code=CODE&redirect_uri=" + CALLBACK + "";


}
