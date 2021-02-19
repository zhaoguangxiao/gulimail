package com.atguigu.gulimail.controller;

import com.atguigu.gulimail.elasticsearch.GulimailElasticsearchMainApplication;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = GulimailElasticsearchMainApplication.class)
@RunWith(SpringRunner.class)
public class TestCase {


    @Autowired
    private RestHighLevelClient esRestClient;

    @Test
    public void contextLoags(){
        System.out.println(esRestClient);
    }
}
