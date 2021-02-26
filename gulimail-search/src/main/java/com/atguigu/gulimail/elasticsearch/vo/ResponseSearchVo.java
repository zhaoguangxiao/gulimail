package com.atguigu.gulimail.elasticsearch.vo;


import com.atguigu.common.to.es.SkuESMode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索返回给页面的实体
 */
@Data
public class ResponseSearchVo {


    private String searchKeyWord; //保存搜索关键字
    private List<SkuESMode> products; //查询到所有的商品信息
    //分页信息
    private Integer pageNum; //当前页面
    private Long total; //总记录数
    private Integer totalPages; //总页码
    //所有涉及的品牌信息
    private List<BrandVo> brands; //当前查询到的结果所有涉及到的品牌
    private List<AttrVo> attrs; //当前查询到的所有属性
    private List<CatelogVo> catelogs; //当前查询到的结果所有涉及到的分类

    private List<NavsVo> navs = new ArrayList<>(); //面包屑导航
    private List<Long> attrIds = new ArrayList<>(); //把筛选属性添加进 便于前端面包屑


    @Data
    public static class NavsVo {
        private String navName;  //名称
        private String navValue; //value
        private String navHref;  //跳转url
    }

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatelogVo {
        private Long catalogId;
        private String catalogName;
    }
}
