package com.atguigu.gulimail.cart.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 */
@Data
public class ShoppingCartVo {


    private List<ShoppingItems> shoppingItems;

    @Setter(AccessLevel.NONE) //不生成set 方法
    @Getter(AccessLevel.NONE) //不生成get 方法
    private Integer countNum; //商品数量

    @Setter(AccessLevel.NONE) //不生成set 方法
    @Getter(AccessLevel.NONE) //不生成get 方法
    private Integer countType; //商品类型数量


    @Setter(AccessLevel.NONE) //不生成set 方法
    @Getter(AccessLevel.NONE) //不生成get 方法
    private BigDecimal totalAmount; //商品总价

    @Setter(AccessLevel.NONE) //不生成set 方法
    private BigDecimal reduce = new BigDecimal("0.00"); //减免的价格


    public Integer getCountNum() {
        if (this.shoppingItems.isEmpty()) return 0;
        int count = 0;
        for (ShoppingItems item : this.shoppingItems) {
            count += item.getCount();
        }
        return count;
    }


    public Integer getCountType() {
        if (this.shoppingItems.isEmpty()) return 0;
        int count = 0;
        for (ShoppingItems item : this.shoppingItems) {
            count += 1;
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal decimal = new BigDecimal("0");
        if (!shoppingItems.isEmpty()) {
            //1 计算购物项总价
            for (ShoppingItems item : this.shoppingItems) {
                BigDecimal totalPrice = item.getTotalPrice();
                decimal = decimal.add(totalPrice);
            }
        }
        //2减去 优惠总价
        BigDecimal bigDecimal = decimal.subtract(getReduce());
        return bigDecimal;
    }
}
