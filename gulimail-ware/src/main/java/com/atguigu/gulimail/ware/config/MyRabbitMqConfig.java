package com.atguigu.gulimail.ware.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;


@Slf4j
@Configuration
public class MyRabbitMqConfig {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 使用json序列化,进行消息转换
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange",
                true,
                false);
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue",
                true,
                false,
                false);
    }

    @Bean
    public Queue stockDelayQueue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", "stock-event-exchange");
        map.put("x-dead-letter-routing-key", "stock.release");
        map.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue",
                true,
                false,
                false,
                map);
    }

    @Bean
    public Binding stockRelease() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null
        );
    }

    @Bean
    public Binding stockLocked() {
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null
        );
    }

}
