package com.atguigu.gulimail.member.service.impl;

import com.atguigu.common.vo.GithubEntityVo;
import com.atguigu.gulimail.member.entity.UserLevelEntity;
import com.atguigu.gulimail.member.exception.PhoneExistException;
import com.atguigu.gulimail.member.exception.UserNameExistException;
import com.atguigu.gulimail.member.service.UserLevelService;
import com.atguigu.gulimail.member.vo.UserLoginVo;
import com.atguigu.gulimail.member.vo.UserRegisterVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.catalina.User;
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


    @Autowired
    private UserDao userDao;


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


    @Override
    public UserEntity login(UserLoginVo userLoginVo) {
        UserEntity userEntity = userDao.selectOneByUserNameOrPhone(userLoginVo);
        if (null != userEntity) {
            String password = userEntity.getPassword();
            boolean matches = new BCryptPasswordEncoder().matches(userLoginVo.getLoginPassword(), password);
            if (matches) {
                //登录成功
                return userEntity;
            } else {
                //登录失败用户名密码错误
                return null;
            }
        }
        return null;
    }


    @Override
    public UserEntity findOneByUserNameOrPhone(UserLoginVo userLoginVo) {
        return userDao.selectOneByUserNameOrPhone(userLoginVo);
    }


    @Override
    public UserEntity githubRegister(GithubEntityVo githubEntityVo) {
        UserEntity entity = new UserEntity();
        //设置默认等级
        UserLevelEntity defaultStatus = userLevelService.getDefaultStatus();
        entity.setLevelId(defaultStatus.getId());

        entity.setUsername(githubEntityVo.getName());
        entity.setEmail(githubEntityVo.getEmail());
        entity.setHeader(githubEntityVo.getAvatar_url());
        entity.setCity(githubEntityVo.getLocation());
        entity.setCreateTime(new Date());
        //设置社交用户唯一id
        entity.setOnlyId(githubEntityVo.getId());

        this.save(entity);
        return entity;
    }

    @Override
    public UserEntity socialContactByOnlyId(GithubEntityVo githubEntityVo) {
        return this.getOne(new QueryWrapper<UserEntity>().eq("only_id", githubEntityVo.getId()));
    }


    @Override
    public UserEntity updateUserEntityByOnlyId(GithubEntityVo githubEntityVo) {
        UserEntity entity = new UserEntity();
        entity.setAccessToken(githubEntityVo.getAccessToken());
        this.update(entity, new UpdateWrapper<UserEntity>().eq("only_id", githubEntityVo.getId()));
        return socialContactByOnlyId(githubEntityVo);
    }
}