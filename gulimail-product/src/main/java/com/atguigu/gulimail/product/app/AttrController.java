package com.atguigu.gulimail.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimail.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimail.product.service.ProductAttrValueService;
import com.atguigu.gulimail.product.vo.AttrResponseVo;
import com.atguigu.gulimail.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimail.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 商品属性
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 16:19:05
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {


    @Autowired
    private AttrService attrService;


    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    @GetMapping(value = "/base/listforspu/{spuId}")
    public R baseListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.baseListForSpu(spuId);
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrResponseVo attrVo = attrService.getAttrVoById(attrId);
        return R.ok().put("attr", attrVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttrVO(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo) {
        attrService.updateAttrVo(attrVo);
        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    public R updateBySpuId(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entityList) {
        productAttrValueService.updateBySpuId(spuId,entityList);
        return R.ok();
    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }


    /**
     * @param params
     * @param catelogId
     * @return 返回全部基本属性
     */
    @GetMapping("/base/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrService.baseListPage(params, catelogId, ProductConstant.AttrConstantEnum.ATTR_TYPE_BASE.getCode());
        return R.ok().put("page", page);
    }


    /**
     * @return 返回全部销售属性
     */
    @GetMapping("/sale/list/{catelogId}")
    public R getClassifiedSaleAttributes(@RequestParam Map<String, Object> params,
                                         @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrService.baseListPage(params, catelogId, ProductConstant.AttrConstantEnum.ATTR_TYPE_SALE.getCode());
        return R.ok().put("page", page);
    }

}
