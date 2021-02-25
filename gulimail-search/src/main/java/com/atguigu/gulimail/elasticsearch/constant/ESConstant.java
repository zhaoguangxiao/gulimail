package com.atguigu.gulimail.elasticsearch.constant;

/**
 * 常量工具类
 *
 * @Data 2021年2月19日16:28:54
 */
public class ESConstant {

    public static final String PRODUCT_INDEX = "gulimail_product"; //sku 数据在 es中索引
    public static final Integer PRODUCT_PAGE_SIZE = 15; //SKU 数据在es 中的索引


    //聚合名称
    public static final String PRODUCT_ATTR_CATEGORY_TOTAL = "catelog_aggs"; //在es 中聚合分类的总名称
    public static final String PRODUCT_ATTR_CATEGORY_NAME_AGGS= "catelog_name_aggs"; //在es 中聚合分类的名称
    public static final String PRODUCT_ATTR_BRAND_NAME = "brand_aggs"; //在es 中聚合品牌总名称
    public static final String PRODUCT_ATTR_BRAND_NAME_AGGS = "brand_name_aggs"; //在es 中聚合品牌名称
    public static final String PRODUCT_ATTR_BRAND_IMG_AGGS = "brand_img_aggs"; //在es 中聚合品牌图片路径


    public static final String PRODUCT_ATTR_AGGS = "attr_aggs"; //在es 中聚合属性
    public static final String PRODUCT_ATTR_ID_AGGS = "attr_id_aggs"; //在es 中聚合属性
    public static final String PRODUCT_ATTR_NAME_AGGS = "attr_name_aggs"; //在es 中聚合属性
    public static final String PRODUCT_ATTR_VALUE_AGGS = "attr_value_aggs"; //在es 中聚合属性


}
