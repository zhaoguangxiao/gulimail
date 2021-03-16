package com.atguigu.gulimail.order.listener;


import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
@RabbitListener(queues = {"order.release.queue"})
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;


    /** 订单释放和库存解锁
     *  现在的流程是    订单创建成功 ->订单解锁
     *                库存锁定 ------------>库存解锁
     *   这样是存在问题的
     *    如果订单创建成功 ---(机器卡顿,消息延迟等原因)-->订单解锁
     *    库存锁定----------->库存解锁 提前完成
     * @param orderEntity
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void orderCloseHandler(OrderEntity orderEntity, Message message, Channel channel) throws IOException {
        log.info("订单时间过期,准备释放订单...");
        try {
            orderService.orderClose(orderEntity);
            //手动接受
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //拒收消息
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
