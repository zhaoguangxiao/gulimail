package com.atguigu.gulimail.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 库存工作单
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
@Data
@TableName("wms_ware_order_bill_detail")
public class WareOrderBillDetailEntity implements Serializable {
    //1 锁定
    public static final Integer LOCK_STOCK = 1;
    //2解锁
    public static final Integer UNLOCKING_STOCK = 2;
    //3库存已经扣减
    public static final Integer STOCK_DEDUCTION = 3;

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;
}
