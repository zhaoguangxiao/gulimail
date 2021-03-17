package com.atguigu.gulimail.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.coupon.dao.SeckillSkuDao;
import com.atguigu.gulimail.coupon.entity.SeckillSkuEntity;
import com.atguigu.gulimail.coupon.service.SeckillSkuService;
import org.springframework.util.StringUtils;


@Service("seckillSkuService")
public class SeckillSkuServiceImpl extends ServiceImpl<SeckillSkuDao, SeckillSkuEntity> implements SeckillSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SeckillSkuEntity> queryWrapper = new QueryWrapper<>();
        Object promotionSessionId = params.get("promotionSessionId");
        //场次id不为空
        if (!StringUtils.isEmpty(promotionSessionId)) {
            queryWrapper.eq("promotion_session_id", promotionSessionId);
        }
        IPage<SeckillSkuEntity> page = this.page(
                new Query<SeckillSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    @Override
    public List<SeckillSkuEntity> listEntityByPromotionSessionId(Long promotionSessionId) {
        return this.list(new QueryWrapper<SeckillSkuEntity>().eq("promotion_session_id", promotionSessionId));
    }
}