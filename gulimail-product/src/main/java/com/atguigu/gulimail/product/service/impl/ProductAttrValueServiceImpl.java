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

import com.atguigu.gulimail.product.dao.ProductAttrValueDao;
import com.atguigu.gulimail.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimail.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateBySpuId(Long spuId, List<ProductAttrValueEntity> entityList) {
        //1 删除之前spuId 之前的数
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //2 新增之后修改的数据
        List<ProductAttrValueEntity> collect = entityList.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }


    @Override
    public List<ProductAttrValueEntity> baseListForSpu(Long spuId) {
        QueryWrapper<ProductAttrValueEntity> entityQueryWrapper = new QueryWrapper<>();
        entityQueryWrapper.eq("spu_id", spuId);
        return this.list(entityQueryWrapper);
    }

    @Override
    public ProductAttrValueEntity findByCategoryIdAndAttrId(Long spuId, Long attrId) {
        return this.getOne(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).eq("attr_id", attrId));
    }
}