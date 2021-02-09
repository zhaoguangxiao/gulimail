package com.atguigu.gulimail.ware.service;

import com.atguigu.gulimail.ware.vo.RequestPurchaseFinshVo;
import com.atguigu.gulimail.ware.vo.RequestPurchaseVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:45
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceive(Map<String, Object> params);

    void savePurchaseVo(RequestPurchaseVo requestPurchaseVo);

    void received(Long[] ids);

    void completePurchaseVo(RequestPurchaseFinshVo requestPurchaseFinshVo);

}

