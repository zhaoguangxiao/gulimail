package com.atguigu.gulimail.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
@Data
@TableName("wms_purchase_detail")
public class PurchaseDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 新建
     */
    public static final Integer CREATE_STATUS = 0;

    /**
     * 已分配
     */
    public static final Integer ALLOCATED_STATUS = 1;
    /**
     * 正在采购
     */
    public static final Integer STAY_PURCHASE_STATUS = 2;

    /**
     * 已完成
     */
    public static final Integer COMPLETED_STATUS = 3;
    /**
     * 采购失败
     */
    public static final Integer PURCHASE_EXCEPTION_STATUS = 4;


    /**
     *
     */
    @TableId
    private Long id;
    /**
     * 采购单id
     */
    private Long purchaseId;
    /**
     * 采购商品id
     */
    private Long skuId;
    /**
     * 采购数量
     */
    private Integer skuNum;
    /**
     * 采购金额
     */
    private BigDecimal skuPrice;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
     */
    private Integer status;

}
