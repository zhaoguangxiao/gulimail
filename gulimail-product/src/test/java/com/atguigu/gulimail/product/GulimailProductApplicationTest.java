package com.atguigu.gulimail.product;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.BrandEntity;
import com.atguigu.gulimail.product.service.BrandService;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimailProductApplicationTest {


    @Autowired
    private BrandService brandService;


    @Test
    public void findBrand(){
        BrandEntity entity = new BrandEntity();
        entity.setName("锤子");
        boolean save = brandService.save(entity);
        Assert.assertTrue(save);
    }

}
