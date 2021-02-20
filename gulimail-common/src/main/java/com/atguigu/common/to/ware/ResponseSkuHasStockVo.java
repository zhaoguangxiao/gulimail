package com.atguigu.common.to.ware;

import lombok.Data;

/**
 * 返回库存的vo实体
 */
@Data
public class ResponseSkuHasStockVo {

    private Long skuId;
    private Boolean hasStock;

}
