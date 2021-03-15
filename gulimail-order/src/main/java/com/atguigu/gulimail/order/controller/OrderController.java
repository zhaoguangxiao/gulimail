package com.atguigu.gulimail.order.controller;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.entity.RefundInfoEntity;
import com.atguigu.gulimail.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


/**
 * 订单
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:41:32
 */

@Slf4j
@RestController
@RequestMapping("order/order")
@RabbitListener(queues = "java-hello-queue")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id) {
        OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order) {
        orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order) {
        orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids) {
        orderService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }


    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn) {
        Integer status = orderService.getOrderStatus(orderSn);
        return R.ok().setData(status);
    }


    /**
     * 订单服务启动多个,同一消息,只能有一个客户端接受到
     * 只有当前消息处理完成,才能接受第二个消息
     * RabbitListener 可以在类上|方法上(监听那些队列即可)
     * RabbitHandler 只能在方法上 (重载不同的消息类型 )
     * 只要消费端确认(保证每个消息被正确消费,此时才可以从broker删除这个消息)
     * 1),默认为自动确认模式,只要消息收到,客户端会自动确认,服务端就会移除这个消息 问题? 我们收到很多消息,自动回复给服务器ack,只有一个消息处理成功,然后宕机了,发生了消息丢失
     * 2),手动确认消息--只要我们没有明确告诉mq 已经被签收,没有给他ack 这个消息一直是 unacked 即使consumer宕机 ,这个消息也不会丢失,会重新变为 ready ,下一次有新的 consumer 进来重新发给他
     *
     * @param message
     * @param entity
     * @param channel
     */
    @RabbitHandler
    public void getMessage(Message message, RefundInfoEntity entity, Channel channel) {
        log.info("消息队列接收到的消息为 {}", entity);
        log.info("消息队列接收到的message {}", message.toString());
        log.info("消息队列接收到的channel {}", channel);

        long tag = message.getMessageProperties().getDeliveryTag();
        //签收货物 非批量签收
        try {
            channel.basicAck(tag, false);
            //退货 requeue true 发回服务器 服务器重新入队 false 丢弃
            //channel.basicNack(tag, false, true);
        } catch (IOException e) {
            log.error("网络中断了 {}", e.getMessage());
        }
    }

}
