package com.atguigu.gulimail.member.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.member.dao.GrowthHistoryDao;
import com.atguigu.gulimail.member.entity.GrowthHistoryEntity;
import com.atguigu.gulimail.member.service.GrowthHistoryService;


@Service("growthHistoryService")
public class GrowthHistoryServiceImpl extends ServiceImpl<GrowthHistoryDao, GrowthHistoryEntity> implements GrowthHistoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GrowthHistoryEntity> page = this.page(
                new Query<GrowthHistoryEntity>().getPage(params),
                new QueryWrapper<GrowthHistoryEntity>()
        );

        return new PageUtils(page);
    }

}