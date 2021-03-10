package com.atguigu.gulimail.ware.service.impl;

import com.atguigu.gulimail.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimail.ware.service.PurchaseDetailService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.gulimail.ware.vo.PurchaseItemDoneVo;
import com.atguigu.gulimail.ware.vo.RequestPurchaseFinshVo;
import com.atguigu.gulimail.ware.vo.RequestPurchaseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.PurchaseDao;
import com.atguigu.gulimail.ware.entity.PurchaseEntity;
import com.atguigu.gulimail.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Slf4j
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnReceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("status", PurchaseEntity.CREATE_STATUS).or().eq("status", PurchaseEntity.ALLOCATED_STATUS);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * @param requestPurchaseVo
     */
    @Transactional
    @Override
    public void savePurchaseVo(RequestPurchaseVo requestPurchaseVo) {
        Long purchaseId = requestPurchaseVo.getPurchaseId();
        if (null == purchaseId) {
            //1 新建采购单
            PurchaseEntity entity = new PurchaseEntity();
            entity.setStatus(PurchaseEntity.CREATE_STATUS);
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            this.save(entity);

            requestPurchaseVo.setPurchaseId(entity.getId());
            updatePurchaseStatus(requestPurchaseVo);
        }else {
            //确认采购单状态是否正确
            PurchaseEntity entity = this.getById(purchaseId);
            if (entity.getStatus() == PurchaseEntity.CREATE_STATUS || entity.getStatus() == PurchaseEntity.ALLOCATED_STATUS) {
                updatePurchaseStatus(requestPurchaseVo);
            }
        }
    }

    private void updatePurchaseStatus(RequestPurchaseVo requestPurchaseVo) {
        List<Long> items = requestPurchaseVo.getItems();
        Long finalPurchaseId = requestPurchaseVo.getPurchaseId();

        //判断采购项状态是否 正确
        //1查询当前采购项下全部的采购单  2判断采购单状态是否为0 1
        //List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listAvailableStatus(items);

        if (!CollectionUtils.isEmpty(items)) {
            List<PurchaseDetailEntity> purchaseDetailEntityList = items.stream().map(item -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(item);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(PurchaseDetailEntity.ALLOCATED_STATUS);
                return detailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(purchaseDetailEntityList);

            //更新日期
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(requestPurchaseVo.getPurchaseId());
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }


    @Transactional
    @Override
    public void received(Long[] ids) {
        //1 确认当前采购单是新建或者已分配状态
        List<Long> longs = Arrays.asList(ids);
        List<PurchaseEntity> collect = longs.stream().map(item -> {
            PurchaseEntity purchaseEntity = this.getById(item);
            //非空判断
            return null == purchaseEntity ? new PurchaseEntity() : purchaseEntity;
        }).filter(each -> {
            return each.getStatus() == PurchaseEntity.CREATE_STATUS || each.getStatus() == PurchaseEntity.ALLOCATED_STATUS;
        }).map(item -> {
            item.setStatus(PurchaseEntity.RECEIVED_STATUS);
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //2 改变采购单的状态
        this.updateBatchById(collect);
        //3 改变采购项的状态

        collect.forEach(each -> {
            List<PurchaseDetailEntity> detailEntities = purchaseDetailService.listDetailByPurchaseId(each.getId());
            if (!CollectionUtils.isEmpty(detailEntities)) {
                List<PurchaseDetailEntity> purchaseDetailEntities = detailEntities.stream().map(item -> {
                    PurchaseDetailEntity entity = new PurchaseDetailEntity();
                    entity.setId(item.getId());
                    entity.setStatus(PurchaseDetailEntity.STAY_PURCHASE_STATUS);
                    return entity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(purchaseDetailEntities);
            }
        });
    }


    @Transactional
    @Override
    public void completePurchaseVo(RequestPurchaseFinshVo requestPurchaseFinshVo) {
        Boolean flag = true;

        //采购单id
        Long id = requestPurchaseFinshVo.getId();
        //2 改变该采购项 状态
        List<PurchaseItemDoneVo> items = requestPurchaseFinshVo.getItems();
        if (!CollectionUtils.isEmpty(items)) {
            List<PurchaseDetailEntity> updates = new ArrayList<>();
            for (PurchaseItemDoneVo each : items) {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                if (each.getStatus() == PurchaseDetailEntity.PURCHASE_EXCEPTION_STATUS) {
                    //采购失败
                    flag = false;
                    detailEntity.setStatus(each.getStatus());
                } else {
                    //采购成功
                    detailEntity.setStatus(PurchaseDetailEntity.COMPLETED_STATUS);
                    //进行入库操作
                    PurchaseDetailEntity detailServiceById = purchaseDetailService.getById(each.getItemId());
                    //3 将成功的进行入库操作
                    wareSkuService.addStock(detailServiceById.getSkuId(),detailServiceById.getWareId(),detailServiceById.getSkuNum());

                }
                detailEntity.setId(each.getItemId());
                updates.add(detailEntity);
            }
            //进行更新操作
            purchaseDetailService.updateBatchById(updates);

            //改变采购单状态
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(id);
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(flag ? PurchaseEntity.COMPLETED_STATUS : PurchaseEntity.EXCEPTION_STATUS);
            this.updateById(purchaseEntity);
        }

    }
}