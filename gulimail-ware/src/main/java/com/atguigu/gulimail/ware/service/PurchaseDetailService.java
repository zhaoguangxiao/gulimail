package com.atguigu.gulimail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long purchaseId);

    /**
     * 查询全部状态为 0 或 1 可用的采购项
     *
     * @param items  purchase_id
     * @return
     */
    List<PurchaseDetailEntity> listAvailableStatus(List<Long> items);



}

