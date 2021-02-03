package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 微服务 a -> b 服务传递json数据对象
 */
@Data
public class SkuBoundsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
