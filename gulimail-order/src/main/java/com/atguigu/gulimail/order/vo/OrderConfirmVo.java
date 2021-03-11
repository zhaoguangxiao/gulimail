package com.atguigu.gulimail.order.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderConfirmVo {

    /**
     * 收货地址列表
     */
    private List<UserAddressVo> userAddressVoList;

    /**
     * 购物项详细信息
     */
    private List<OrderItemVo> orderItemVos;

    //发票...
    //优惠卷信息
    //积分信息查询
    private Integer integration;

    @Setter(AccessLevel.NONE) //不生成set 方法
    @Getter(AccessLevel.NONE) //不生成get 方法
    private BigDecimal totalPrice; //订单总额

    @Setter(AccessLevel.NONE) //不生成set 方法
    @Getter(AccessLevel.NONE) //不生成get 方法
    private BigDecimal payPrice;  //应付总额

    private Map<Long, Boolean> stocks; //是否有仓库


    private String orderToken; //防止重复提交的令牌

    public BigDecimal getTotalPrice() {
        BigDecimal decimal = new BigDecimal("0");
        if (!CollectionUtils.isEmpty(orderItemVos)) {
            for (OrderItemVo itemVo : orderItemVos) {
                BigDecimal multiply = itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount()));
                decimal = decimal.add(multiply);
            }
            return decimal;
        }
        return decimal;
    }

    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }

    public Integer getCountShoppingCart() {
        int count = 0;
        if (!CollectionUtils.isEmpty(orderItemVos)) {
            for (OrderItemVo orderItemVo : orderItemVos) {
                count += orderItemVo.getCount();
            }
        }
        return count;
    }
}
