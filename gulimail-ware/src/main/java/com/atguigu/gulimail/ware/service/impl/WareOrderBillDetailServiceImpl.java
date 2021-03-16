package com.atguigu.gulimail.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.WareOrderBillDetailDao;
import com.atguigu.gulimail.ware.entity.WareOrderBillDetailEntity;
import com.atguigu.gulimail.ware.service.WareOrderBillDetailService;


@Service("wareOrderBillDetailService")
public class WareOrderBillDetailServiceImpl extends ServiceImpl<WareOrderBillDetailDao, WareOrderBillDetailEntity> implements WareOrderBillDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderBillDetailEntity> page = this.page(
                new Query<WareOrderBillDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderBillDetailEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<WareOrderBillDetailEntity> listByTaskIdAndStatus(Long taskId, Integer lockStock) {
        return this.list(new QueryWrapper<WareOrderBillDetailEntity>().eq("task_id", taskId).eq("lock_status", lockStock));
    }
}