package com.atguigu.gulimail.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimail.product.entity.AttrEntity;
import com.atguigu.gulimail.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimail.product.service.AttrService;
import com.atguigu.gulimail.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimail.product.vo.ResponseAttrGroupWithAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.AttrGroupDao;
import com.atguigu.gulimail.product.entity.AttrGroupEntity;
import com.atguigu.gulimail.product.service.AttrGroupService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;


    @Autowired
    private AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((item) -> {
                item.like("attr_group_name", key).or().like("descript", key);
            });
        }
        // catelogId ==0
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );

            return new PageUtils(page);
        } else {
            //catelogId != 0
            //select * from pms_attr_group where catelog_id=225 and (attr_group_name like '%主%' or descript like '%主%')
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrEntity> findAttrGroupAndRelationship(Long attrgroupId) {
        List<AttrEntity> entityList = new ArrayList<>();
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        if (!CollectionUtils.isEmpty(entities)) {
            entities.forEach(item -> {
                AttrEntity attrEntity = attrService.getOne(new QueryWrapper<AttrEntity>().eq("attr_id", item.getAttrId()));
                entityList.add(attrEntity);
            });
        }
        return entityList;
    }

    @Override
    public void deleteGroupAndRelationship(AttrGroupRelationVo[] attrGroupRelationVos) {

        List<AttrAttrgroupRelationEntity> collect = Arrays.asList(attrGroupRelationVos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());

        attrAttrgroupRelationService.deleteBatchRelation(collect);

    }

    @Override
    public PageUtils findNoattrRelation(Map<String, Object> params, Long attrgroupId) {
        //1 当前分组只能关联自己所属分类属性
        AttrGroupEntity groupEntity = this.getOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", attrgroupId));
        //获取当前分类id
        Long catelogId = groupEntity.getCatelogId();
        //2 当前分组只能关联别的分组没有引用的属性
        //2.1 找到当前分类下面的其它分组 --并且不能是当前分类
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIds = groupEntities.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2 找到这些分组关联的属性

        QueryWrapper<AttrAttrgroupRelationEntity> entityQueryWrapper = new QueryWrapper<>();
        if (null != attrGroupIds && attrGroupIds.size() > 0) {
            entityQueryWrapper.in("attr_group_id", attrGroupIds);
        }
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(entityQueryWrapper);
        List<Long> attrIds = relationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //2.3 从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrConstantEnum.ATTR_TYPE_BASE.getCode());
        if (null != attrIds && attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        //获取模糊参数
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //模糊查询
            queryWrapper.like("attr_name", key);
        }
        IPage<AttrEntity> page = attrService.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public void saveGroupAndRelation(List<AttrGroupRelationVo> attrGroupRelationVo) {
        List<AttrAttrgroupRelationEntity> collect = attrGroupRelationVo.stream().map(item -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entity);
            return entity;
        }).collect(Collectors.toList());

        attrAttrgroupRelationService.saveBatch(collect);

    }

    @Override
    public Boolean deleteByIds(List<Long> asList) {

        List<AttrAttrgroupRelationEntity> entityList = new ArrayList<>();
        //判断是否存在关联关系
        asList.forEach(item -> {
            List<AttrAttrgroupRelationEntity> relationServiceOne = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", item));
            if (!CollectionUtils.isEmpty(relationServiceOne)) {
                entityList.addAll(relationServiceOne);
            }
        });
        if (CollectionUtils.isEmpty(entityList)) {
            //进行批量删除操作
            return this.removeByIds(asList);
        }
        return false;
    }

    @Override
    public List<ResponseAttrGroupWithAttrVo> getAttrGroupWithAttr(Long catelogId) {
        List<AttrGroupEntity> entities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<ResponseAttrGroupWithAttrVo> collect = entities.stream().map(item -> {
            ResponseAttrGroupWithAttrVo attrVo = new ResponseAttrGroupWithAttrVo();
            BeanUtils.copyProperties(item,attrVo);
            //获取每个分组下面的具体属性
            List<AttrEntity> groupAndRelationship = findAttrGroupAndRelationship(item.getAttrGroupId());
            if (!groupAndRelationship.isEmpty()){
                attrVo.setAttrs(groupAndRelationship);
            }
            return attrVo;
        }).collect(Collectors.toList());
        return collect;
    }
}