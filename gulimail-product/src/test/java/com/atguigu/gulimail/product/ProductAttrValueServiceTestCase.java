package com.atguigu.gulimail.product;


import com.atguigu.gulimail.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimail.product.service.ProductAttrValueService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = GulimailProductApplicationMain10000.class)
@RunWith(SpringRunner.class)
public class ProductAttrValueServiceTestCase {


    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Test
    public void findByCategoryIdAndAttrIdTest(){
        ProductAttrValueEntity idAndAttrId = productAttrValueService.findByCategoryIdAndAttrId(13L, 11L);
        Assert.assertNotNull(idAndAttrId);
    }
}
