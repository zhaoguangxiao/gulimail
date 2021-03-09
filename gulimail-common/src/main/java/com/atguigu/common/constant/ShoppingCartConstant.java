package com.atguigu.common.constant;

public class ShoppingCartConstant {

    public static final String TEMPORARY_COOKIE_SHOPPING_CART_KEY = "user-key"; //临时用户 cookie 名称key
    public static final Integer COOKIE_TMP_MAX_TIME = 2592000;  //以秒为单位 60*60*24*30;
    public static final String COOKIE_DOMAIN_NAME = "gulimail.com"; //临时用户 cookie 名称key

    public static final String REDIS_CART_KEY_PREFIX = "gulimail:cart:"; //购物车前缀
}
