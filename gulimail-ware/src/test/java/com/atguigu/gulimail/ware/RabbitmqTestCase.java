package com.atguigu.gulimail.ware;

import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockSuccessTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = GuliMailWareApplication11000.class)
@RunWith(SpringRunner.class)
public class RabbitmqTestCase {



    @Autowired
    private RabbitTemplate rabbitTemplate;




    @Test
    public void sendMessage(){
        StockLockSuccessTo lockSuccessTo = new StockLockSuccessTo();
        lockSuccessTo.setId(1L);
        StockDetailTo detailTo = new StockDetailTo();
        lockSuccessTo.setStockDetailTo(detailTo);
        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockSuccessTo);
    }



}
