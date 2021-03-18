package com.atguigu.gulimail.order.service;

import com.atguigu.common.to.mq.QuickOrderSeckillTo;
import com.atguigu.gulimail.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:41:32
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * @return 订单确认页面返回需要的数据
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    ResponseSubmitOrderVo submitOrder(OrderSubmitVo orderSubmitVo);

    /**
     * 根据订单号拿到订单状态
     *
     * @param orderSn
     * @return
     */
    Integer getOrderStatus(String orderSn);


    OrderEntity getOrderEntityByOrderSn(String orderSn);

    /**
     * 关闭订单
     *
     * @param orderEntity
     */
    void orderClose(OrderEntity orderEntity);

    /**
     * 根据订单号返回订单详细信息
     *
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);


    /**
     * 如果支付成功异步通知服务
     *
     * @param payAsyncVo
     * @return 成功返回 success 失败返回error
     */
    String handlerAlipayed(PayAsyncVo payAsyncVo);


    int updateOrderStatus(String orderSn,Integer status);

    void quickOrderSeckill(QuickOrderSeckillTo quickOrderSeckillTo);

}

