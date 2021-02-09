package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import java.util.List;

/**  接受前端传递的参数
 * @author zgx
 */
@Data
public class RequestPurchaseVo {

    private Long purchaseId;
    private List<Long> items;
}
