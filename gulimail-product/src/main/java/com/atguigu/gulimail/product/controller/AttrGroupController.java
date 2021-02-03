package com.atguigu.gulimail.product.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.gulimail.product.entity.AttrEntity;
import com.atguigu.gulimail.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimail.product.service.CategoryService;
import com.atguigu.gulimail.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimail.product.vo.ResponseAttrGroupWithAttrVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimail.product.entity.AttrGroupEntity;
import com.atguigu.gulimail.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 属性分组
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-20 16:19:05
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {


    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;


    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrGroupService.queryPage(params);
        return R.ok().put("page", page);
    }

    @RequestMapping("/list/{catelogId}")
    public R pageList(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.getCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        log.info("执行删除逻辑");
        Boolean deleteByIds = attrGroupService.deleteByIds(Arrays.asList(attrGroupIds));
        if (deleteByIds) return R.ok();
        else return R.error(BizCodeEnume.DELETION_FAILED.getCode(), BizCodeEnume.DELETION_FAILED.getMessage());
    }

    /**
     * @return 返回当前分组的全部关联关系
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R findAttrGroupAndRelationship(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntity = attrGroupService.findAttrGroupAndRelationship(attrgroupId);
        return R.ok().put("data", attrEntity);
    }


    @PostMapping("/attr/relation/delete")
    public R deleteGroupAndRelationship(@RequestBody AttrGroupRelationVo[] attrGroupRelationVo) {
        attrGroupService.deleteGroupAndRelationship(attrGroupRelationVo);
        return R.ok();
    }

    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
     *
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R findNoattrRelation(@RequestParam Map<String, Object> params,
                                @PathVariable("attrgroupId") Long attrgroupId) {
        PageUtils page = attrGroupService.findNoattrRelation(params, attrgroupId);
        return R.ok().put("page", page);
    }


    @PostMapping("/attr/relation")
    public R saveGroupAndRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVo) {
        attrGroupService.saveGroupAndRelation(attrGroupRelationVo);
        return R.ok();
    }


    /**
     * 获取分类下所有分组&关联属性
     *
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttr(@PathVariable("catelogId") Long catelogId) {
        List<ResponseAttrGroupWithAttrVo> attrVo = attrGroupService.getAttrGroupWithAttr(catelogId);
        return R.ok().put("data", attrVo);
    }

}
