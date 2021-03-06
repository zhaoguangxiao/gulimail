package com.atguigu.gulimail.product.app;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.gulimail.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimail.product.entity.SpuInfoEntity;
import com.atguigu.gulimail.product.service.SpuInfoService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * spu信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 16:19:05
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = spuInfoService.queryPageCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id) {
        SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo spuSaveVo) {
        //spuInfoService.save(spuInfo);
        spuInfoService.saveSpuSaveVo(spuSaveVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo) {
        spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids) {
        spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 商品上架功能
     *
     * @param spuId
     * @return
     */
    @PostMapping(value = "/{spuId}/up")
    public R shopUp(@PathVariable("spuId") Long spuId) {
        spuInfoService.up(spuId);
        return R.ok();
    }


    @GetMapping("/{skuId}/id")
    public R getSpuEntityBySkuId(@PathVariable("skuId") Long skuId) {
        SpuInfoEntity spuInfoEntity = spuInfoService.getSpuEntityBySkuId(skuId);
        return R.ok().setData(spuInfoEntity);
    }


}
