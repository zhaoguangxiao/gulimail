package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuWareHasStockVo {

    private Long skuId;
    private Integer count;
    private List<Long> wareId;

}
