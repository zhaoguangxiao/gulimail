package com.atguigu.gulimail.member.service;

import com.atguigu.gulimail.member.exception.PhoneExistException;
import com.atguigu.gulimail.member.exception.UserNameExistException;
import com.atguigu.gulimail.member.vo.UserRegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.member.entity.UserEntity;

import java.util.Map;

/**
 * 用户表
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:25:51
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void registerUser(UserRegisterVo userRegisterVo);

    void checkPhoneAndUserName(String phone, String username) throws UserNameExistException, PhoneExistException;
}

