package com.atguigu.gulimail.order.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


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


    /**
     * 只要消息抵达 broker ack就是true
     * 定制rabbitmqTemplate
     */
    @PostConstruct //在构造器对象创建完在来构建这个方法
    public void initRabbitmqTemplate() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * @param correlationData  当前消息的唯一关联数据(这个是消息的唯一的id)
             * @param ack 消息是否成功还是失败
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("CorrelationData = {}", correlationData);
                log.info("ack = {}", ack);
                log.info("cause = {}", cause);
            }
        });

        /**
         * 设置消息抵达队列的确认回调
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /** 只要消息没有投递给指定的队列,就触发这个失败的回调
             * @param message  那个消息投递失败,详细内容
             * @param replyCode 回复状态码
             * @param replyText 回复文本内容
             * @param exchange 这个消息发送给那个交换机
             * @param routingKey 这个消息用的那个路由件
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("message {} ", message);
                log.info("replyCode {} ", replyCode);
                log.info("replyText {} ", replyText);
                log.info("exchange {} ", exchange);
                log.info("routingKey {} ", routingKey);
            }
        });
    }


}
