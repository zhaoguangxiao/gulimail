package com.atguigu.gulimail.ware;

import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.gulimail.ware.service.WareSkuService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = GuliMailWareApplication11000.class)
@RunWith(SpringRunner.class)
public class WareSkuServiceTestCase {

    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 根据sku 查询是否存在库存
     */
    @Test
    public void getSkuHasStockTest() {
        List<ResponseSkuHasStockVo> skuHasStock = wareSkuService.getSkuHasStock(Arrays.asList(27L));
        Assert.assertTrue(!skuHasStock.isEmpty());
    }


}
