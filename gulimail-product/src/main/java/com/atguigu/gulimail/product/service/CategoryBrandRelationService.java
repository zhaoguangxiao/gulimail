package com.atguigu.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveCategoryBrandRelationEntity(CategoryBrandRelationEntity categoryBrandRelation);


    /**
     * 根据品牌id 更新 品牌 name
     *
     * @param brandId
     * @param name
     */
    void updateBrandNameByBrandId(Long brandId, String name);

    /**
     * @param catId
     * @param name
     */
    void updateCategoryBycategoryId(Long catId, String name);
}

