package com.atguigu.gulimail.product.service;

import com.atguigu.gulimail.product.vo.AttrResponseVo;
import com.atguigu.gulimail.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * add attr vo
     *
     * @param attr
     */
    void saveAttrVO(AttrVo attr);


    /**
     * @param params
     * @param catelogId
     * @param attrType 类型
     * @return
     */
    PageUtils baseListPage(Map<String, Object> params, Long catelogId,Integer attrType);

    AttrResponseVo getAttrVoById(Long attrId);

    void updateAttrVo(AttrVo attrVo);
}

