package com.atguigu.gulimail.product.dao;

import com.atguigu.gulimail.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimail.product.vo.ResponseItemSkuVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<ResponseItemSkuVo.ItemSkuSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
