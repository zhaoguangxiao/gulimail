package com.atguigu.gulimail.product.service.impl;

import com.atguigu.gulimail.product.vo.ResponseItemSkuVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimail.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimail.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<ResponseItemSkuVo.ItemSkuSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        //分析当前spu 有多少个sku ,所有sku设计到的属性集合

        //1 通过 spuid 在pms_sku_info 表拿到所有的skuid 信息

        //2 通过 skuid 在 pms_sku_sale_attr_value 拿到销售属性信息

        return this.baseMapper.getSaleAttrsBySpuId(spuId);
    }
}