package com.atguigu.gulimail.ware.service;

import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.to.mq.StockLockSuccessTo;
import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.gulimail.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<ResponseSkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 为某个订单锁定库存
     *
     * @param wareSkuLockVo
     * @return
     */
    Boolean orderStockLocks(WareSkuLockVo wareSkuLockVo);

    /** 解锁库存
     * @param stockLockSuccessTo
     */
    public void unlockStock(StockLockSuccessTo stockLockSuccessTo);

    /**解锁订单的库存
     * @param orderEntityTo
     */
    public void unlockStock(OrderEntityTo orderEntityTo);
}

