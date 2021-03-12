package com.atguigu.gulimail.order.vo;

import com.atguigu.gulimail.order.entity.OrderEntity;
import lombok.Data;

/**
 * 下单整个返回数据
 */
@Data
public class ResponseSubmitOrderVo {


    private OrderEntity orderEntity;
    private Integer code; //错误状态码 0 代表成功 不是0 代表错误



}
