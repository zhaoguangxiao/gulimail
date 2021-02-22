package com.atguigu.gulimail.product.service;

import com.atguigu.gulimail.product.vo.ResponseCategoryLog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * @return 返回分类以树形结构显示
     */
    List<CategoryEntity> treeStructure();

    void removeMenuByIds(List<Long> catIds);

    Long[] getCatelogPath(Long catelogId);

    /**
     * update category details
     *
     * @param category
     */
    void updateCategoryEntityById(CategoryEntity category);

    /**
     * @return 全部一级分类
     */
    List<CategoryEntity> getLevelCategorys();

    Map<String, List<ResponseCategoryLog2Vo>> getCatelogJson();

}

