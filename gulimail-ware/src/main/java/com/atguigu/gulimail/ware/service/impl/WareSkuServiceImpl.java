package com.atguigu.gulimail.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockSuccessTo;
import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.dao.WareSkuDao;
import com.atguigu.gulimail.ware.entity.WareOrderBillDetailEntity;
import com.atguigu.gulimail.ware.entity.WareOrderBillEntity;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import com.atguigu.gulimail.ware.exception.NoStockException;
import com.atguigu.gulimail.ware.feign.OrderFeignService;
import com.atguigu.gulimail.ware.feign.SkuInfoService;
import com.atguigu.gulimail.ware.service.WareOrderBillDetailService;
import com.atguigu.gulimail.ware.service.WareOrderBillService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.gulimail.ware.vo.OrderItemVo;
import com.atguigu.gulimail.ware.vo.SkuWareHasStockVo;
import com.atguigu.gulimail.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.atguigu.common.enume.OrderStatusEnum.CANCLED;
import static com.atguigu.gulimail.ware.entity.WareOrderBillDetailEntity.LOCK_STOCK;
import static com.atguigu.gulimail.ware.entity.WareOrderBillDetailEntity.UNLOCKING_STOCK;


@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private WareOrderBillService wareOrderBillService;

    @Autowired
    private WareOrderBillDetailService wareOrderBillDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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


    /**
     * @param wareSkuLockVo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderStockLocks(WareSkuLockVo wareSkuLockVo) {
        //保存库存工作单详情
        WareOrderBillEntity entity = new WareOrderBillEntity();
        entity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderBillService.save(entity);


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
            //1  如果每一个商品都锁成功,将当前商品锁定了几件的工作单,详情记录发送给了mq
            //2  如果锁定失败 前面保存的工作单数据回滚了,发送出去的消息即使要解锁记录,因为数据库查不到id,所以不用解锁
            Boolean flag = true;
            for (SkuWareHasStockVo skuWareHasStockVo : collect) {
                Boolean skuStocked = false;
                Long skuId = skuWareHasStockVo.getSkuId();
                List<Long> wareId = skuWareHasStockVo.getWareId();
                if (!CollectionUtils.isEmpty(wareId)) {
                    for (Long wareIds : wareId) {
                        //success 1 fails 0
                        Integer count = wareSkuDao.lockSkuStock(skuId, wareIds, skuWareHasStockVo.getCount());
                        if (count == 1) {
                            skuStocked = true;
                            //锁库存成功
                            WareOrderBillDetailEntity detailEntity = new WareOrderBillDetailEntity();
                            detailEntity.setSkuId(skuId);
                            detailEntity.setWareId(wareIds);
                            detailEntity.setSkuNum(skuWareHasStockVo.getCount());
                            detailEntity.setLockStatus(LOCK_STOCK);
                            //保存商品订购单id
                            detailEntity.setTaskId(entity.getId());
                            wareOrderBillDetailService.save(detailEntity);
                            //告诉 rabbitmq 锁定一些库存
                            StockLockSuccessTo lockSuccessTo = new StockLockSuccessTo();
                            lockSuccessTo.setId(entity.getId());
                            StockDetailTo detailTo = new StockDetailTo();
                            BeanUtils.copyProperties(detailEntity, detailTo);
                            lockSuccessTo.setStockDetailTo(detailTo);
                            rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockSuccessTo);
                            break;
                        } else {
                            //锁定库存失败
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


    public void unlockStock(StockLockSuccessTo stockLockSuccessTo) {
        //工作单id
        Long billId = stockLockSuccessTo.getId();
        //拿到库存工作单id
        Long aLong = stockLockSuccessTo.getStockDetailTo().getId();
        //1查询数据关于这个订单锁库存消息
        WareOrderBillDetailEntity detailServiceById = wareOrderBillDetailService.getById(aLong);

        //detailServiceById !=null 只能证明库存服务没有问题,到底要不要解锁,还要看订单
        //如果订单没有创建 无需解锁
        if (null != detailServiceById) {
            //需要解锁
            WareOrderBillEntity serviceById = wareOrderBillService.getById(billId);
            //拿到订单号
            String orderSn = serviceById.getOrderSn();
            //根据订单号 查询订单状态
            R status = orderFeignService.getOrderStatus(orderSn);
            if (Integer.parseInt(status.get("code").toString()) == 0) {
                Integer orderStatus = status.getData(new TypeReference<Integer>() {
                });
                //订单不存在 ,库存服务锁好,但是订单服务调用其它服务炸了 回滚了
                if (null == orderStatus || orderStatus == CANCLED.getCode()) {
                    //当前库存工作单状态为 已锁定的时候才可以解锁
                    if (detailServiceById.getLockStatus() == LOCK_STOCK) {
                        //订单已经被取消了 解锁库存
                        StockDetailTo stockDetailTo = stockLockSuccessTo.getStockDetailTo();
                        //库存解锁
                        stockUnlocking(stockDetailTo.getSkuId(), stockDetailTo.getWareId(), stockDetailTo.getSkuNum(),detailServiceById.getId());
                    }
                }
            } else {
                throw new RuntimeException("远程服务调用失败");
            }

        } else {
            //无需解锁库存 ,库存回滚了
        }
    }

    private void stockUnlocking(Long skuId, Long wareId, Integer num,Long detailId) {
        this.baseMapper.unlockStock(skuId, wareId, num);
        //更新库存工作单与工作单详情 的状态
        WareOrderBillDetailEntity entity = new WareOrderBillDetailEntity();
        entity.setId(detailId);
        entity.setLockStatus(UNLOCKING_STOCK);
        wareOrderBillDetailService.updateById(entity);
    }




    /**
     * 防止订单服务卡顿,导致订单一直改不了状态,库存一直改不了,然后库存消息优先到期,然后查订单状态一直是新建状态,什么都不做就都走了
     * 导致卡顿的订单,永远不能解锁自己的库存
     *
     * @param orderEntityTo
     */
    @Transactional
    @Override
    public void unlockStock(OrderEntityTo orderEntityTo) {
        //1 查询订单最新状态
        WareOrderBillEntity wareOrderBillEntity = wareOrderBillService.getEntityByOrderSn(orderEntityTo.getOrderSn());
        if (null != wareOrderBillEntity) {
            //按照库存工作单,找出全部锁定状态的
            List<WareOrderBillDetailEntity> wareOrderBillDetailEntityList = wareOrderBillDetailService.listByTaskIdAndStatus(wareOrderBillEntity.getId(), LOCK_STOCK);
            if (!CollectionUtils.isEmpty(wareOrderBillDetailEntityList)) {
                for (WareOrderBillDetailEntity entity : wareOrderBillDetailEntityList) {
                    stockUnlocking(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
                }
            }
        }
    }
}