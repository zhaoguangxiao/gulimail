package com.atguigu.gulimail.product.vo;

import com.atguigu.gulimail.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * 分组下面全部属性vo
 *
 * @author Administrator
 */
@Data
public class ResponseAttrGroupWithAttrVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    List<AttrEntity> attrs;

}
