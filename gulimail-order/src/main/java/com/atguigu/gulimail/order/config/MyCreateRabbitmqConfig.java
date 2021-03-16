package com.atguigu.gulimail.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * rabbitmq 创建交换机 用spring的方式
 * 1个交换机 2个队列 2个绑定关系
 */
@Configuration
public class MyCreateRabbitmqConfig {


    /**
     * @return
     * @Bean 容易中 队列,交换机,绑定关系, 都会自动创建 ,前提是rabbitmq没有会自动创建
     */
    @Bean
    public Queue orderDelayQueue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", "order-event-exchange");
        map.put("x-dead-letter-routing-key", "order.release.order");
        map.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue",
                true,
                false,
                false,
                map);
    }

    @Bean
    public Queue orderReleaseQueue() {
        return new Queue("order.release.queue",
                true,
                false,
                false);
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange",
                true,
                false);
    }

    @Bean
    public Binding orderCreateOrder() {
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    @Bean
    public Binding orderReleaseOrder() {
        return new Binding("order.release.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    /**
     * 订单释放直接和库存释放进行绑定
     *
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.other.#", null);
    }


}
