package com.atguigu.gulimail.product.entity;

import lombok.Data;

import java.util.List;

@Data
public class ItemSkuSaleAttrEntity {

    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdEntity> attrValueWithSkuIdEntities;
}
