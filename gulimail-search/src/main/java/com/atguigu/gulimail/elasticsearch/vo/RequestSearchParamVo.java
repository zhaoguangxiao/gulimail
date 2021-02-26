package com.atguigu.gulimail.elasticsearch.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有的查询条件
 */
@Data
public class RequestSearchParamVo {

    private String keyword; //全文匹配关键字
    private Long catalog3Id; //三级分类id
    /**
     * sort=saleCount_asc/desc
     * sort=skuPric_asc/desc
     * sort=hotScore_asc/desc
     * 任选其一
     */
    private String sort; //排序条件

    private Integer hasStock; //是否有货 0-无货 1-有货
    /**
     * skuPrice=1_500 1-500之内的
     * skuPrice=_500 500之内的
     * skuPrice=500_ 500以上的
     */
    private String skuPrice;//价格区间
    private List<Long> brandId; //多个品牌id,按照品牌进行查询


    //商品属性
    private List<String> attrs; //按照属性进行筛选  &attrs=1_5寸&8寸&attrs=2_15寸&18


    private Integer pageNum;//页码

    private String queryString; //远程的所有查询条件


}
