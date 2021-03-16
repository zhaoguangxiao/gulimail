package com.atguigu.gulimail.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.atguigu.gulimail.order.vo.PayAsyncVo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.order.dao.PaymentInfoDao;
import com.atguigu.gulimail.order.entity.PaymentInfoEntity;
import com.atguigu.gulimail.order.service.PaymentInfoService;

import static com.atguigu.gulimail.order.vo.PayAsyncVo.END_OF_TRANSACTION;
import static com.atguigu.gulimail.order.vo.PayAsyncVo.TRANSACTION_PAYMENT_SUCCESSFUL;


@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoDao, PaymentInfoEntity> implements PaymentInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PaymentInfoEntity> page = this.page(
                new Query<PaymentInfoEntity>().getPage(params),
                new QueryWrapper<PaymentInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(PayAsyncVo payAsyncVo) {
        PaymentInfoEntity entity = new PaymentInfoEntity();
        //设置创建时间
        entity.setCreateTime(new Date());
        //设置通知时间
        entity.setCallbackTime(DateUtil.parseDate(payAsyncVo.getNotify_time()));
        //设置订单号
        entity.setOrderSn(payAsyncVo.getOut_trade_no());
        //设置支付宝交易流水号
        entity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        //设置交易状态
        entity.setPaymentStatus(payAsyncVo.getTrade_status());

        this.save(entity);
    }
}