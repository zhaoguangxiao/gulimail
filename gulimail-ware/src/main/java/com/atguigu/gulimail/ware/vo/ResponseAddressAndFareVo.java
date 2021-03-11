package com.atguigu.gulimail.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 返回当前收货人信息与运费信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAddressAndFareVo {
    private BigDecimal fare;
    private UserAddressVo userAddressVo;
}
