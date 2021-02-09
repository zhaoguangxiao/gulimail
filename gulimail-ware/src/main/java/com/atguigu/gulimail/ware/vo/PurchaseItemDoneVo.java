package com.atguigu.gulimail.ware.vo;

import lombok.Data;

/**
 * {itemId:2,status:4,reason:""}
 */
@Data
public class PurchaseItemDoneVo {

    private Long itemId;
    private Integer status;
    private String reason;
}
