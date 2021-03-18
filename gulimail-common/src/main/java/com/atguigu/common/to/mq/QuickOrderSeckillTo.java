package com.atguigu.common.to.mq;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickOrderSeckillTo {

    private String OrderSn; //订单号
    private Long skuId; //skuid
    private Long promotionSessionId; //活动场次id
    private BigDecimal seckillPrice; //秒杀价格
    private Integer num; //购买数量
    private Long userId; //会员id

}
