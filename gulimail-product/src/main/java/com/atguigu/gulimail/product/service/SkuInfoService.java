package com.atguigu.gulimail.product.service;

import com.atguigu.gulimail.product.vo.ResponseItemSkuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkuBySpuId(Long spuId);



    /** 使用异步编排进行查询
     * @param skuid
     * @return 详情页面的全部数据
     */
    ResponseItemSkuVo item(Long skuid) throws ExecutionException, InterruptedException;
}

