package com.atguigu.gulimail.cart.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingItems {

    private Long skuId;
    private Boolean check; //是否被选中
    private String title; //商品标题
    private String defaultImage; //商品默认图片
    private BigDecimal price; //商品价格
    private Integer count; //几件

    @Getter(AccessLevel.NONE) //不为 小计生成get 方法 重写get方法
    private BigDecimal totalPrice; //商品小计
    private List<String> skuAttr; //属性集合

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal("" + this.count));
    }
}
