package com.atguigu.gulimail.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {

    private Long addressId; //收货地址id
    private Integer payType; //支付方式
    //无需提交需要购买的商品,去购物车在获取一遍即可
    //优惠,发票...
    private String orderToken; //防重令牌
    private BigDecimal payPrice;//应付价格 验证价格
    //用户相关信息 在session中取出
    private String note; //订单备注

}
