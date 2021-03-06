package com.atguigu.gulimail.product;

import com.atguigu.gulimail.product.service.SkuSaleAttrValueService;
import com.atguigu.gulimail.product.vo.ResponseItemSkuVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = GulimailProductApplicationMain10000.class)
@RunWith(SpringRunner.class)
public class SkuSaleAttrValueServiceTestCase {


    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;


    @Test
    public void getSaleAttrsBySpuId(){
        List<ResponseItemSkuVo.ItemSkuSaleAttrVo> attrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(11L);
        Assert.assertTrue(attrsBySpuId.isEmpty());
    }
}
