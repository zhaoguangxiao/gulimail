package com.atguigu.gulimail.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.member.dao.UserAddressDao;
import com.atguigu.gulimail.member.entity.UserAddressEntity;
import com.atguigu.gulimail.member.service.UserAddressService;


@Service("userAddressService")
public class UserAddressServiceImpl extends ServiceImpl<UserAddressDao, UserAddressEntity> implements UserAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserAddressEntity> page = this.page(
                new Query<UserAddressEntity>().getPage(params),
                new QueryWrapper<UserAddressEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<UserAddressEntity> listUserAddressByUserId(Long userId) {
        return this.list(new QueryWrapper<UserAddressEntity>().eq("user_id", userId));
    }
}