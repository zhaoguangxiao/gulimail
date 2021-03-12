package com.atguigu.gulimail.order.to;


import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderTo {

    private OrderEntity orderEntity; //订单实体
    private List<OrderItemEntity> itemVos; //订单项
    private BigDecimal payPrice; //订单应付价格
    private BigDecimal fare; //运费


}
