package com.atguigu.gulimail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.ware.entity.WareOrderBillDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 库存工作单
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
public interface WareOrderBillDetailService extends IService<WareOrderBillDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);


    List<WareOrderBillDetailEntity> listByTaskIdAndStatus(Long taskId, Integer lockStock);

}

