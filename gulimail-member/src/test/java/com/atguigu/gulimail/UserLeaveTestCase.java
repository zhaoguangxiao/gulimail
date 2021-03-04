package com.atguigu.gulimail;

import com.atguigu.gulimail.member.GuliMailMemberApplicationMain8000;
import com.atguigu.gulimail.member.entity.UserLevelEntity;
import com.atguigu.gulimail.member.service.UserLevelService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@SpringBootTest(classes = GuliMailMemberApplicationMain8000.class)
@RunWith(SpringRunner.class)
public class UserLeaveTestCase {


    @Autowired
    private UserLevelService userLevelService;


    /**
     *  初始化会员等级
     */
    @Test
    public void init(){
        UserLevelEntity entity = new UserLevelEntity();
        entity.setName("普通会员");
        entity.setDefaultStatus(1);
        entity.setFreeFreightPoint(new BigDecimal(299));
        entity.setPriviledgeFreeFreight(10);
        entity.setPriviledgeMemberPrice(0);
        userLevelService.save(entity);
    }
    @Test
    public void getDefaultStatus(){
        UserLevelEntity defaultStatus = userLevelService.getDefaultStatus();
        Assert.assertNotNull(defaultStatus);
    }


}
