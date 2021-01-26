package com.atguigu.gulimail.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 品牌
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 15:51:58
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotEmpty(message = "品牌名不能为空")
    private String name;
    /**
     * 品牌logo地址
     */
    @NotEmpty(message = "品牌logo地址不能为空")
    @URL(message = "品牌logo地址不合法")
    private String logo;
    /**
     * 介绍
     */
    @NotEmpty(message = "品牌介绍不能为空")
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotEmpty(message = "品牌检索首字母不能为空")
    @Pattern(regexp = "/^[a-zA-Z]$/]", message = "品牌检索首字母必须为一个a-z字母")
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "品牌排序不能为空")
    @Min(value = 0)
    private Integer sort;

}
