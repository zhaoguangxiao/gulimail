package com.atguigu.gulimail.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.gulimail.ware.exception.NoStockException;
import com.atguigu.gulimail.ware.vo.ResponseOrderStockLockVo;
import com.atguigu.gulimail.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import static com.atguigu.common.exception.BizCodeEnume.NOT_STOCK_LOCK_EXCEPTION;


/**
 * 商品库存
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 查看sku是否有库存
     *
     * @return
     */
    @PostMapping(value = "/hashStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<ResponseSkuHasStockVo> skuHasStock = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().setData(skuHasStock);
    }


    /**
     * 为某个订单锁库存
     * 库存解锁的场景
     *  1), 下订单成功,订单过期没有支付被系统自动取消,被用户手动取消订单
     *  2), 下订单成功,库存锁定成功,接下来的业务调用失败,导致丁订单回滚,之前锁定的库存就要解锁
     *
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/lock/order")
    public R orderStockLocks(@RequestBody WareSkuLockVo wareSkuLockVo) {
        try {
            Boolean flag = wareSkuService.orderStockLocks(wareSkuLockVo);
            if (flag) return R.ok();
            else return R.error();
        } catch (NoStockException e) {
            return R.error(NOT_STOCK_LOCK_EXCEPTION.getCode(), NOT_STOCK_LOCK_EXCEPTION.getMessage());
        }
    }

}
