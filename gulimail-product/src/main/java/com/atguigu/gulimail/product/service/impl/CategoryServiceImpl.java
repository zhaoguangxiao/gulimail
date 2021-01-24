package com.atguigu.gulimail.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.CategoryDao;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> treeStructure() {
        //查出全部分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //找出1级分类
        List<CategoryEntity> collect = selectList.stream().
                filter(each -> each.getParentCid() == 0)
                .map(each -> {
                    //找出全部子分类 并赋值
                    each.setSubcategoryList(getStructure(each, selectList));
                    return each;
                }).sorted((each1, each2) -> (each1.getSort() == null ? 0 : each1.getSort()) - (each2.getSort() == null ? 0 : each2.getSort())
                ).collect(Collectors.toList());
        //找出1级分类下面的子分类

        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        //TODO 1检查当前删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(catIds);
    }

    private List<CategoryEntity> getStructure(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(each -> {
            return each.getParentCid().equals(root.getCatId());
        }).map(each -> {
            //找出子分类下面的子分类 --递归查找
            each.setSubcategoryList(getStructure(each, all));
            return each;
        }).sorted((each1, each2) -> {
            return (each1.getSort() == null ? 0 : each1.getSort()) - (each2.getSort() == null ? 0 : each2.getSort());
        }).collect(Collectors.toList());


    }

}