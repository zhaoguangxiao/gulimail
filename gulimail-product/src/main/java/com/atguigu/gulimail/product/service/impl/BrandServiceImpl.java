package com.atguigu.gulimail.product.service.impl;

import com.atguigu.gulimail.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.BrandDao;
import com.atguigu.gulimail.product.entity.BrandEntity;
import com.atguigu.gulimail.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNoneEmpty(key)) {
            wrapper.like("name", key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * rewrite update method details
     *
     * @param entity this object
     * @return
     */

    @Override
    @Transactional
    public void updateBrandEntityById(BrandEntity entity) {
        // update this
        this.updateById(entity);
        //update brand 品牌
        categoryBrandRelationService.updateBrandNameByBrandId(entity.getBrandId(), entity.getName());

        //TODO
    }


    @Override
    public List<BrandEntity> getBybrandIds(List<Long> brandIds) {
        return this.list(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
    }
}