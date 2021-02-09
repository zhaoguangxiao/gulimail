package com.atguigu.gulimail.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimail.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimail.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();


        //key:
        //status:
        //wareId

        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }


        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long purchaseId) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("purchase_id", purchaseId);
        return this.list(queryWrapper);
    }

    @Override
    public List<PurchaseDetailEntity> listAvailableStatus(List<Long> items) {
        List<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();

        //查询每个采购单下面全部的采购项
        items.forEach(item -> {
            QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("purchase_id", item);
            purchaseDetailEntities.addAll(this.list(queryWrapper));
        });

        //得到状态为 0 或者 1的采购项
        return purchaseDetailEntities.stream().filter(item -> {
            return item.getStatus() == PurchaseDetailEntity.CREATE_STATUS || item.getStatus() == PurchaseDetailEntity.ALLOCATED_STATUS;
        }).collect(Collectors.toList());

    }
}