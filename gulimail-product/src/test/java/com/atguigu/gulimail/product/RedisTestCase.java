package com.atguigu.gulimail.product;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = GulimailProductApplicationMain10000.class)
@RunWith(SpringRunner.class)
public class RedisTestCase {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    public void redisAdd(){
        stringRedisTemplate.opsForValue().set("name","zs");
    }


    @Test
    public void redisGet(){
        String name = stringRedisTemplate.opsForValue().get("name");
        Assert.assertTrue("zs".equals(name));
    }
}
