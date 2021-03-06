package com.atguigu.gulimail.product.vo;

import com.atguigu.gulimail.product.entity.AttrEntity;
import lombok.Data;

/**
 * save Entity
 *
 * @author Administrator
 */
@Data
public class AttrVo extends AttrEntity {


    private Long attrGroupId;

    /**
     * 分类完整路径
     */
    private Long[] catelogPath;
}
