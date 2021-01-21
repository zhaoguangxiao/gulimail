package com.atguigu.gulimail.member.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.member.dao.UserStatisticsDao;
import com.atguigu.gulimail.member.entity.UserStatisticsEntity;
import com.atguigu.gulimail.member.service.UserStatisticsService;


@Service("userStatisticsService")
public class UserStatisticsServiceImpl extends ServiceImpl<UserStatisticsDao, UserStatisticsEntity> implements UserStatisticsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserStatisticsEntity> page = this.page(
                new Query<UserStatisticsEntity>().getPage(params),
                new QueryWrapper<UserStatisticsEntity>()
        );

        return new PageUtils(page);
    }

}