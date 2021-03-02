package com.atguigu.gulimail.product;

import com.atguigu.gulimail.product.service.AttrGroupService;
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
public class AttrGroupServiceTestCase {

    @Autowired
    private AttrGroupService attrGroupService;

    @Test
    public void getAttrGroupWithAttrsBySpuId(){
        List<ResponseItemSkuVo.ItemSpuBaseAttrVo> withAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(13L, 225L);
        Assert.assertTrue(!withAttrsBySpuId.isEmpty());
    }
}
