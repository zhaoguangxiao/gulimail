package com.atguigu.gulimail.product.service;

import com.atguigu.gulimail.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存商品接口
     *
     * @param spuSaveVo
     */
    void saveSpuSaveVo(SpuSaveVo spuSaveVo);
}

