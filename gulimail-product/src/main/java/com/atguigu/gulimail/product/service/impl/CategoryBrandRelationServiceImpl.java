package com.atguigu.gulimail.product.service.impl;

import com.atguigu.gulimail.product.entity.BrandEntity;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.BrandService;
import com.atguigu.gulimail.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimail.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimail.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveCategoryBrandRelationEntity(CategoryBrandRelationEntity categoryBrandRelation) {
        // brandId catelogId
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandService.getById(brandId);
        //set
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        //save
        this.save(categoryBrandRelation);
    }


    @Override
    public void updateBrandNameByBrandId(Long brandId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setBrandName(name);
        this.update(entity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updateCategoryBycategoryId(Long catId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setCatelogName(name);
        this.update(entity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
    }


    @Override
    public List<BrandEntity> findBrandEntityByCatlogId(Long catId) {
        List<CategoryBrandRelationEntity> relationEntities = this.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<BrandEntity> collect = relationEntities.stream().map(item -> {
            return brandService.getById(item.getBrandId());
        }).collect(Collectors.toList());
        return collect;
    }
}