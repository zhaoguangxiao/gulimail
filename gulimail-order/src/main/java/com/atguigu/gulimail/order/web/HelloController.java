package com.atguigu.gulimail.order.web;

import cn.hutool.core.util.IdUtil;
import com.atguigu.gulimail.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;

@Controller
public class HelloController {


    @Autowired
    RabbitTemplate rabbitTemplate;


    @GetMapping("/detail.html")
    public String detailPage() {
        return "detail";
    }


    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }

    @GetMapping("/pay.html")
    public String payPage() {
        return "pay";
    }


    @GetMapping("/create/mq")
    @ResponseBody
    public String createMessage() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(IdUtil.simpleUUID());
        orderEntity.setCreateTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderEntity);
        return "success";
    }


    @RabbitListener(queues = "order.release.queue")
    public void releaseRabbitmq(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        System.out.println("关闭订单为 " + orderEntity.getCreateTime());
        System.out.println("关闭订单为 " + orderEntity.getOrderSn());
        //确认收到消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
