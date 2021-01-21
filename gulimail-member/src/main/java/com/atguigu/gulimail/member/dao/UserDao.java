package com.atguigu.gulimail.member.dao;

import com.atguigu.gulimail.member.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:25:51
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
	
}
