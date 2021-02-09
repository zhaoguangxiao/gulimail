package com.atguigu.gulimail.ware.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.gulimail.ware.vo.RequestPurchaseFinshVo;
import com.atguigu.gulimail.ware.vo.RequestPurchaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimail.ware.entity.PurchaseEntity;
import com.atguigu.gulimail.ware.service.PurchaseService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 采购信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:45
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @GetMapping(value = "/unreceive/list")
    public R unReceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageUnReceive(params);
        return R.ok().put("page", page);
    }

    /**
     * 合并整单
     *
     * @param requestPurchaseVo
     * @return
     */
    @PostMapping(value = "/merge")
    public R merge(@RequestBody RequestPurchaseVo requestPurchaseVo) {
        purchaseService.savePurchaseVo(requestPurchaseVo);
        return R.ok();
    }


    /**
     * 领取采购
     *
     * @param ids
     * @return
     */
    @PostMapping(value = "/received")
    public R received(@RequestBody Long[] ids) {
        purchaseService.received(ids);
        return R.ok();
    }

    @PostMapping(value = "/done")
    public R completePurchaseVo(@RequestBody RequestPurchaseFinshVo requestPurchaseFinshVo) {
        purchaseService.completePurchaseVo(requestPurchaseFinshVo);
        return R.ok();
    }

}
