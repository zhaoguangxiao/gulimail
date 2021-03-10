package com.atguigu.gulimail.order.service;

import com.atguigu.gulimail.order.vo.OrderConfirmVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:41:32
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * @return 订单确认页面返回需要的数据
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;
}

