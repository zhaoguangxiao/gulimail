package com.atguigu.gulimail.order.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 购物车所有选中的购物项信息
 */
@Data
public class OrderItemVo {

    private Long skuId;
    private Boolean check; //是否被选中
    private String title; //商品标题
    private String defaultImage; //商品默认图片
    private BigDecimal price; //商品价格
    private Integer count; //几件
    private BigDecimal totalPrice; //商品小计
    private List<String> skuAttr; //属性集合
    private BigDecimal weight; //商品重量

}
