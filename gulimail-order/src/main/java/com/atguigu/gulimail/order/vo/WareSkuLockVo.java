package com.atguigu.gulimail.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 锁库存实体类
 */
@Data
public class WareSkuLockVo {

    private String orderSn; //订单号
    private List<OrderItemVo> orderItemVoList; //需要锁的订单项信息


}
