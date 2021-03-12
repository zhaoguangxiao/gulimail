package com.atguigu.gulimail.ware.vo;

import lombok.Data;

/**
 * 库存锁定结果
 */
@Data
public class ResponseOrderStockLockVo {
    private Long skuId; //那件商品
    private Integer count; //锁了几件
    private Boolean locked; //是否锁成功
}
