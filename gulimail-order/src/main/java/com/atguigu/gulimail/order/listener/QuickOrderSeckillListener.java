package com.atguigu.gulimail.order.listener;

import com.atguigu.common.to.mq.QuickOrderSeckillTo;
import com.atguigu.gulimail.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 秒杀监听器,用于快速创建订单
 */
@Slf4j
@Service
@RabbitListener(queues = "order.seckill.order.queue")
public class QuickOrderSeckillListener {

    @Autowired
    private OrderService orderService;


    @RabbitHandler
    public void quickOrderSeckillListener(QuickOrderSeckillTo quickOrderSeckillTo, Message message, Channel channel) throws IOException {
        log.info("准备创建秒杀单的详细信息 {}", quickOrderSeckillTo);
        try {
            orderService.quickOrderSeckill(quickOrderSeckillTo);
            //签收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception exception) {
            //拒收 重新放入队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
