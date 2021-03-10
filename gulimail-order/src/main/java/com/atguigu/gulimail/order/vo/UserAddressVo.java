package com.atguigu.gulimail.order.vo;

import lombok.Data;

/**
 * 用户收货地址vo
 */
@Data
public class UserAddressVo {
    private Long id;
    /**
     * member_id
     */
    private Long userId;
    /**
     * 收货人
     */
    private String name;
    /**
     * 电话
     */
    private String phone;
    /**
     * 右边
     */
    private String postCode;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区
     */
    private String region;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 是否默认地址
     */
    private Integer defaultStatus;
}
