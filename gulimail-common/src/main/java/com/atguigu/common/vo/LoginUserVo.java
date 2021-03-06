package com.atguigu.common.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录用户保存在 spring session
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:25:51
 */
@Data
public class LoginUserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 盐
     */
    private String salt;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 来源
     */
    private Integer sourceType;
    /**
     * 购物积分
     */
    private Integer integration;
    /**
     * 赠送积分
     */
    private Integer growth;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;


    /**
     * 社交账号登录唯一id
     */
    private Long onlyId;

    private String accessToken;

}
