package com.atguigu.common.to.mq;

import lombok.Data;

/**
 * 库存锁定成功的to
 */
@Data
public class StockLockSuccessTo {

    private Long id; //库存工作单id
    private StockDetailTo stockDetailTo; //工作单详情对象


}
