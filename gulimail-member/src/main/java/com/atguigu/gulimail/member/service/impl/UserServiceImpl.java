package com.atguigu.gulimail.member.service.impl;

import com.atguigu.gulimail.member.entity.UserLevelEntity;
import com.atguigu.gulimail.member.exception.PhoneExistException;
import com.atguigu.gulimail.member.exception.UserNameExistException;
import com.atguigu.gulimail.member.service.UserLevelService;
import com.atguigu.gulimail.member.vo.UserRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.member.dao.UserDao;
import com.atguigu.gulimail.member.entity.UserEntity;
import com.atguigu.gulimail.member.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private UserLevelService userLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void registerUser(UserRegisterVo userRegisterVo) {
        //检查用户名 和手机号是否唯一
        checkPhoneAndUserName(userRegisterVo.getPhone(), userRegisterVo.getUsername());

        //设置实体等相关信息
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(userRegisterVo, entity);
        entity.setCreateTime(new Date());
        //设置默认等级
        UserLevelEntity defaultStatus = userLevelService.getDefaultStatus();
        entity.setLevelId(defaultStatus.getId());

        //设置密码 --加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(userRegisterVo.getPassword());
        entity.setSalt(null);
        entity.setPassword(password);
        this.save(entity);
    }


    @Override
    public void checkPhoneAndUserName(String phone, String username) throws UserNameExistException, PhoneExistException {
        int usernameCount = this.count(new QueryWrapper<UserEntity>().eq("username", username));
        if (usernameCount > 0) {
            throw new UserNameExistException();
        }
        int phoneCount = this.count(new QueryWrapper<UserEntity>().eq("phone", phone));
        if (phoneCount > 0) {
            throw new PhoneExistException();
        }
    }
}