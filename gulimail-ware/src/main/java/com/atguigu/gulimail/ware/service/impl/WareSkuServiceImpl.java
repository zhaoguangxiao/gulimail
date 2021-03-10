package com.atguigu.gulimail.ware.service.impl;

import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.dao.WareSkuDao;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import com.atguigu.gulimail.ware.feign.SkuInfoService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private WareSkuDao wareSkuDao;


    @Autowired
    private SkuInfoService skuInfoService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        //skuId
        //wareId
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(list)) {
            //新增
            WareSkuEntity entity = new WareSkuEntity();
            entity.setSkuId(skuId);
            entity.setWareId(wareId);
            entity.setStock(skuNum);
            entity.setStockLocked(0);
            //todo 自己catch 异常无需回滚
            //todo 高级部分再讲
            try {
                R info = skuInfoService.info(skuId);
                Map<String, Object> date = (Map<String, Object>) info.get("skuInfo");
                entity.setSkuName((String) date.get("skuName"));
            } catch (Exception e) {

            }

            this.save(entity);
        } else {
            //更新
            wareSkuDao.update(skuId, wareId, skuNum);
        }
    }


    @Override
    public List<ResponseSkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(item -> {
            ResponseSkuHasStockVo skuHasStockVo = new ResponseSkuHasStockVo();
            //查询当前sku 的总库存量
            Long count = wareSkuDao.getSkuHasStock(item);
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count == null ? false : count > 0);

            return skuHasStockVo;
        }).collect(Collectors.toList());
    }
}