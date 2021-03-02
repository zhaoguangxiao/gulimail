package com.atguigu.gulimail.product.vo;

import com.atguigu.gulimail.product.entity.AttrValueWithSkuIdEntity;
import com.atguigu.gulimail.product.entity.SkuImagesEntity;
import com.atguigu.gulimail.product.entity.SkuInfoEntity;
import com.atguigu.gulimail.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * 返回详情页需要的全部数据
 */
@Data
public class ResponseItemSkuVo {

    private Boolean hasStock;  //是否有货
    private SkuInfoEntity skuInfoEntity; //sku基本信息
    private List<SkuImagesEntity> skuImagesEntityList; //sku 图片集
    private SpuInfoDescEntity spuInfoDescEntity; //获取spu描述信息
    private List<ItemSkuSaleAttrVo> saleAttrVos; //销售属性集合
    private List<ItemSpuBaseAttrVo> itemSpuBaseAttrVos; //获取spu的规格参数信息

    @Data
    public static class ItemSkuSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdEntity> attrValueWithSkuIdEntities;
    }

    @Data
    public static class ItemSpuBaseAttrVo {
        private String groupName;
        List<SpuBaseAttrVo> spuBaseAttrVos;

    }

    @Data
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }
}
