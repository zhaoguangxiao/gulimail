package com.atguigu.gulimail.product;

import com.atguigu.gulimail.product.entity.BrandEntity;
import com.atguigu.gulimail.product.service.BrandService;
import com.atguigu.gulimail.product.service.CategoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimailProductApplicationTest {


    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;



    @Test
    public void findBrand() {
        BrandEntity entity = new BrandEntity();
        entity.setName("锤子");
        boolean save = brandService.save(entity);
        Assert.assertTrue(save);
    }


    @Test
    public void findCatelogPath(){
        Long[] catelogPath = categoryService.getCatelogPath(225L);
        System.out.println(Arrays.asList(catelogPath));
    }


}
