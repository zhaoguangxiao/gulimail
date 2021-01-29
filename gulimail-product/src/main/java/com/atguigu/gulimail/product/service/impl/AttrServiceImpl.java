package com.atguigu.gulimail.product.service.impl;

import com.atguigu.gulimail.product.dao.AttrGroupDao;
import com.atguigu.gulimail.product.dao.CategoryDao;
import com.atguigu.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimail.product.entity.AttrGroupEntity;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimail.product.vo.AttrResponseVo;
import com.atguigu.gulimail.product.vo.AttrVo;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.AttrDao;
import com.atguigu.gulimail.product.entity.AttrEntity;
import com.atguigu.gulimail.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttrVO(AttrVo attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);
        this.save(entity);
        log.info("AttrId = {}", entity.getAttrId());
        log.info("getAttrGroupId = {}", attr.getAttrGroupId());
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(entity.getAttrId());
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        attrAttrgroupRelationService.save(relationEntity);
    }

    @Override
    public PageUtils baseListPage(Map<String, Object> params, Long catelogId) {
        IPage<AttrEntity> page = null;
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.like("attr_name", key);
        }
        if (catelogId == 0) {
            page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    queryWrapper
            );
        } else {
            page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    queryWrapper.eq("catelog_id", catelogId)
            );
        }

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrResponseVo> collect = records.stream().map(entity -> {
            AttrResponseVo attrResponseVo = new AttrResponseVo();
            //赋值给新对象
            BeanUtils.copyProperties(entity, attrResponseVo);
            //1查询关联表的 groupid是多少
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId()));
            if (null != attrAttrgroupRelationEntity) {
                //2通过groupid 查询出当前对象
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                //3赋值name
                attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            //通过 categoryId 查询出分类对象
            CategoryEntity categoryEntity = categoryDao.selectById(entity.getCatelogId());
            if (null != categoryEntity) {
                //设置分类名称
                attrResponseVo.setCatelogName(categoryEntity.getName());
            }
            //返回新的 attrvo对象
            return attrResponseVo;
        }).collect(Collectors.toList());

        pageUtils.setList(collect);
        return pageUtils;
    }

}