package com.atguigu.gulimail.ware.service.impl;

import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.dao.WareSkuDao;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import com.atguigu.gulimail.ware.exception.NoStockException;
import com.atguigu.gulimail.ware.feign.SkuInfoService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.gulimail.ware.vo.OrderItemVo;
import com.atguigu.gulimail.ware.vo.SkuWareHasStockVo;
import com.atguigu.gulimail.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderStockLocks(WareSkuLockVo wareSkuLockVo) {
        //1 按照下单的收货地址,找到一个就近仓库,锁定库存
        //1 找到每个商品在那个仓库有,就锁库存
        List<OrderItemVo> orderItemVoList = wareSkuLockVo.getOrderItemVoList();
        if (!CollectionUtils.isEmpty(orderItemVoList)) {
            List<SkuWareHasStockVo> collect = orderItemVoList.stream().map(item -> {
                SkuWareHasStockVo hasStockVo = new SkuWareHasStockVo();
                hasStockVo.setSkuId(item.getSkuId());
                hasStockVo.setCount(item.getCount());
                List<Long> wareId = wareSkuDao.listWareIdBySkuId(item.getSkuId());
                hasStockVo.setWareId(wareId);
                return hasStockVo;
            }).collect(Collectors.toList());

            Boolean flag = true;
            for (SkuWareHasStockVo skuWareHasStockVo : collect) {
                Boolean skuStocked = false;
                Long skuId = skuWareHasStockVo.getSkuId();
                List<Long> wareId = skuWareHasStockVo.getWareId();
                if (!CollectionUtils.isEmpty(wareId)) {
                    for (Long wareIds : wareId) {
                        //success 1 fails 0
                        Integer num = wareSkuDao.lockSkuStock(skuId, wareIds, skuWareHasStockVo.getCount());
                        if (num == 0) {
                            //当前库存锁失败,重试下一个仓库
                        } else {
                            //锁库存成功
                            skuStocked = true;
                            //跳出当前循环
                            break;
                        }
                    }
                    if (!skuStocked) {
                        //库存不足异常
                        throw new NoStockException(skuId);
                    }

                } else {
                    //库存不足异常
                    throw new NoStockException(skuId);
                }
            }
        }
        return true;
    }
}