package com.atguigu.gulimail.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.WareDao;
import com.atguigu.gulimail.ware.entity.WareEntity;
import com.atguigu.gulimail.ware.service.WareService;
import org.springframework.util.StringUtils;


@Service("wareService")
public class WareServiceImpl extends ServiceImpl<WareDao, WareEntity> implements WareService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("name", key);
            });
        }

        IPage<WareEntity> page = this.page(
                new Query<WareEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}