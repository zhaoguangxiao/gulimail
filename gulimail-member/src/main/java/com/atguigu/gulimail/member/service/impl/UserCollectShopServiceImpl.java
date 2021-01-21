package com.atguigu.gulimail.member.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.member.dao.UserCollectShopDao;
import com.atguigu.gulimail.member.entity.UserCollectShopEntity;
import com.atguigu.gulimail.member.service.UserCollectShopService;


@Service("userCollectShopService")
public class UserCollectShopServiceImpl extends ServiceImpl<UserCollectShopDao, UserCollectShopEntity> implements UserCollectShopService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserCollectShopEntity> page = this.page(
                new Query<UserCollectShopEntity>().getPage(params),
                new QueryWrapper<UserCollectShopEntity>()
        );

        return new PageUtils(page);
    }

}