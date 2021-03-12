package com.atguigu.gulimail.ware.controller;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.entity.WareEntity;
import com.atguigu.gulimail.ware.service.WareService;
import com.atguigu.gulimail.ware.vo.ResponseAddressAndFareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 仓库信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareController {
    @Autowired
    private WareService wareService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:ware:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:ware:info")
    public R info(@PathVariable("id") Long id) {
        WareEntity ware = wareService.getById(id);

        return R.ok().put("ware", ware);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:ware:save")
    public R save(@RequestBody WareEntity ware) {
        wareService.save(ware);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:ware:update")
    public R update(@RequestBody WareEntity ware) {
        wareService.updateById(ware);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:ware:delete")
    public R delete(@RequestBody Long[] ids) {
        wareService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 根据地址ID 获取邮费和当前用户地址信息
     *
     * @param addrId
     * @return
     */
    @GetMapping("/fare")
    public R getFare(@RequestParam("addrId") Long addrId) {
        ResponseAddressAndFareVo responseAddressAndFareVo = wareService.getFare(addrId);
        return R.ok().setData(responseAddressAndFareVo);
    }

}
