package com.atguigu.gulimail;

import com.atguigu.gulimail.member.GuliMailMemberApplicationMain8000;
import com.atguigu.gulimail.member.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(classes = GuliMailMemberApplicationMain8000.class)
@RunWith(SpringRunner.class)
public class UserServiceTestCase {


    @Autowired
    private UserService userService;


    @Test
    public void md5() {
        String md2Hex = DigestUtils.md2Hex("1234");
        log.info("md5 加密后: {}", md2Hex);


        //盐值salt 加密
        //$1$qqqqqqqq$EN5nqB7pMpv7iKTVW0wrp
        String crypt = Md5Crypt.md5Crypt("1234".getBytes(),"$1$qqqqqqqq");
        log.info("crypt 加密后: {}", crypt);


        //spring
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //$2a$10$xBHAEy/rIhJqqtHw3PFuIOYDzBarpJBAlkZpA3s.OsC0WHQubTZ7O
        String encode = encoder.encode("1234");
        //登录
        boolean matches = encoder.matches("1234", "$2a$10$xBHAEy/rIhJqqtHw3PFuIOYDzBarpJBAlkZpA3s.OsC0WHQubTZ7O");
        log.info("encode 加密后: {}", encode);
        log.info("matches =={}", matches);
    }


}
