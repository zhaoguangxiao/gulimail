package com.atguigu.gulimail.product.service;

import com.atguigu.gulimail.product.entity.AttrEntity;
import com.atguigu.gulimail.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.AttrGroupEntity;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 根据组id 查询出全部属性
     *
     * @param attrgroupId 组id
     * @return
     */
    List<AttrEntity> findAttrGroupAndRelationship(Long attrgroupId);

    /**
     * 删除关联关系
     */
    void deleteGroupAndRelationship(AttrGroupRelationVo[] attrGroupRelationVos);

    PageUtils findNoattrRelation(Map<String, Object> params, Long attrgroupId);

    void saveGroupAndRelation(List<AttrGroupRelationVo> attrGroupRelationVo);

    /** 批量删除
     * @param asList
     * @return
     */
    Boolean deleteByIds(List<Long> asList);
}

