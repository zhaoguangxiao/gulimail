package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RequestPurchaseFinshVo {

    @NotNull(message = "采购单id不能为空")
    private Long id;

    private List<PurchaseItemDoneVo> items;


}
