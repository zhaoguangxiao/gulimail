package com.atguigu.gulimail.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockSuccessTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.entity.WareOrderBillDetailEntity;
import com.atguigu.gulimail.ware.entity.WareOrderBillEntity;
import com.atguigu.gulimail.ware.feign.OrderFeignService;
import com.atguigu.gulimail.ware.service.WareOrderBillDetailService;
import com.atguigu.gulimail.ware.service.WareOrderBillService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.atguigu.common.enume.OrderStatusEnum.CANCLED;

@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
@Service
public class StockReleaseListener {


    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 1),  库存自动解锁
     * 2), 下订单成功,库存自动锁定成功,接下来业务调用失败,导致订单回滚,之前锁定的库存自动解锁
     * 3), 订单失败,锁库存失败
     * 处理库存释放
     * 库存自动解锁
     * 只要解锁库存的消息失败,一定要告诉服务器此次解锁失败的 启动手动ack模式
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockSuccessTo stockLockSuccessTo, Channel channel, Message message) throws IOException {
        log.info("收到库存解锁的消息 {}", stockLockSuccessTo.getId());
        try {
            wareSkuService.unlockStock(stockLockSuccessTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //拒绝接受此消息--重新放入队列让别人继续消费解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }



    @RabbitHandler
    public void handleOrderLockedRelease(OrderEntityTo orderEntityTo, Channel channel, Message message) throws IOException {
        log.info("收到订单关闭,准备解锁库存的消息 订单信息为 : {}", orderEntityTo);
        try {
            wareSkuService.unlockStock(orderEntityTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //拒绝接受此消息--重新放入队列让别人继续消费解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
