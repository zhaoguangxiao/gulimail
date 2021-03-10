package com.atguigu.gulimail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.member.entity.UserAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 收货地址表
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:25:51
 */
public interface UserAddressService extends IService<UserAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户id 查询出全部收货地址
     *
     * @param userId
     * @return
     */
    List<UserAddressEntity> listUserAddressByUserId(Long userId);
}

