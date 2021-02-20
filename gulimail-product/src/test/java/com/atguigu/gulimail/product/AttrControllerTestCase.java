package com.atguigu.gulimail.product;

import com.atguigu.gulimail.product.service.AttrService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = GulimailProductApplicationMain10000.class)
@RunWith(SpringRunner.class)
public class AttrControllerTestCase {


    @Autowired
    private AttrService attrService;


    @Test
    public void getSearchAttrsTest(){
        List<Long> list = Arrays.asList(14L, 15L, 16L);
        List<Long> searchAttrs = attrService.getSearchAttrs(list);

    }

}
