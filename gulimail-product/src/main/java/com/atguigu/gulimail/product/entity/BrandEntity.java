package com.atguigu.gulimail.product.entity;

import com.atguigu.common.vaild.InsertGroup;
import com.atguigu.common.vaild.UpdateGroup;
import com.atguigu.common.validater.FirstLetter;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 * 自定义校验注解:
 * 1),编写一个自定义的校验注解
 * 2), 编写一个自定义的效验器
 * 3),关联自定义的校验器
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @NotNull(message = "修改的时候id不能为空", groups = {UpdateGroup.class})
    @Null(message = "新增的时候不能指定id", groups = {InsertGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotEmpty(message = "品牌名不能为空", groups = {UpdateGroup.class, InsertGroup.class})
    private String name;
    /**
     * 品牌logo地址
     */
    @NotEmpty(message = "品牌logo地址不能为空", groups = {InsertGroup.class})
    @URL(message = "品牌logo地址不合法", groups = {UpdateGroup.class, InsertGroup.class})
    private String logo;
    /**
     * 介绍
     */
    @NotEmpty(message = "品牌介绍不能为空", groups = {InsertGroup.class})
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母
     *
     * @Pattern(regexp = "/^[a-zA-Z]$/]", message = "品牌检索首字母必须为一个a-z字母", groups = {InsertGroup.class, UpdateGroup.class})
     */
    @NotEmpty(message = "品牌检索首字母不能为空", groups = {InsertGroup.class})
    @FirstLetter(groups = {InsertGroup.class, UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "品牌排序不能为空", groups = {InsertGroup.class})
    @Min(value = 0, groups = {InsertGroup.class, UpdateGroup.class})
    private Integer sort;

}
