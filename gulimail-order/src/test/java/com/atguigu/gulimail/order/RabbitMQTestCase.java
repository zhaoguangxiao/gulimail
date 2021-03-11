package com.atguigu.gulimail.order;

import cn.hutool.core.util.IdUtil;
import com.atguigu.gulimail.order.entity.RefundInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;


@Slf4j
@SpringBootTest(classes = GuliMailOrderApplicationMain9000.class)
@RunWith(SpringRunner.class)
public class RabbitMQTestCase {


    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 创建交换机
     */
    @Test
    public void createExchanges() {
        //声明交换机
        amqpAdmin.declareExchange(new DirectExchange("java-hello-exchanges", true, false));
        log.info("交换机创建完成 {}", "java-hello-exchanges");
    }

    @Test
    public void createQueue() {
        amqpAdmin.declareQueue(new Queue("java-hello-queue", true, false, false));
        log.info("队列创建成功{}", "java-hello-queue");
    }


    @Test
    public void createBindingQueue() {
        //String destination,  目的地
        //DestinationType destinationType, 类型
        //String exchange, 交换机
        //String routingKey, 路由件
        //@Nullable Map<String, Object> arguments 其它自定义参数
        //将 exchanges 指定的交换机和 destination 目的地进行绑定 使用routingKey 作为路由件
        amqpAdmin.declareBinding(new Binding("java-hello-queue", Binding.DestinationType.QUEUE, "java-hello-exchanges", "hell-java", null));
        log.info("绑定成功 {}", "java-hello");
    }

    @Test
    public void deleteExchanges() {
        //删除指定绑定
        amqpAdmin.deleteExchange("java-hello-exchanges");
    }

    @Test
    public void deleteQueue() {
        amqpAdmin.deleteQueue("java-hello-queue");
    }


    @Test
    public void sendMessage() {
        String message = "hello word java";
        RefundInfoEntity entity = new RefundInfoEntity();
        entity.setId(1L);
        entity.setOrderReturnId(1L);
        entity.setRefundContent("1");
        entity.setRefundStatus(1);
        entity.setOrderReturnId(1L);
        //如果发送的消息是一个对象,我们会使用对象的序列化机制,将对象写出去,对象必须实现 Serializable 接口
        //2发送对象要使用 json格式
        // CorrelationData 设置消息的唯一id
        rabbitTemplate.convertAndSend("java-hello-exchanges", "hell-java1", entity, new CorrelationData(IdUtil.simpleUUID()));
        log.info("消息发送完成 {}", entity);

    }


}
