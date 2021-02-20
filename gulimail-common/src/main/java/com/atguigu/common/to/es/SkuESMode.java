package com.atguigu.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuESMode {

    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice; //价格
    private String skuImg; //默认图片
    private Long saleCount; //销量
    private Boolean hasStock; //库存
    private Long hotScore;//热度评分
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;


    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }


}
