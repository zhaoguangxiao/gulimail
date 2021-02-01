package com.atguigu.gulimail.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 移除分组与属性关联关系vo
 * [{"attrId":1,"attrGroupId":2}]
 *
 * @author Administrator
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttrGroupRelationVo {

    private Long attrId;
    private Long attrGroupId;


}
