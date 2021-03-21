package com.atguigu.gulimail.product.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.product.dao.SkuInfoDao;
import com.atguigu.gulimail.product.entity.SkuImagesEntity;
import com.atguigu.gulimail.product.entity.SkuInfoEntity;
import com.atguigu.gulimail.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimail.product.feign.SeckillFeignService;
import com.atguigu.gulimail.product.feign.WareSkuFeignService;
import com.atguigu.gulimail.product.service.*;
import com.atguigu.gulimail.product.vo.ResponseItemSkuVo;
import com.atguigu.gulimail.product.vo.SeckillSkuRedisDetailsTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.nashorn.internal.ir.Block;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


    @Autowired
    private SeckillFeignService seckillFeignService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Autowired
    private WareSkuFeignService wareSkuFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        //key:
        //catelogId: 225
        //brandId: 9
        //min: 1
        //max: 2
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        if (!StringUtils.isEmpty(max)) {
            try {
                //判断 结果是否大于等于0
                boolean compare = new BigDecimal(max).compareTo(new BigDecimal(0)) == 1;
                if (compare) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {
                log.error("sku max 转换异常 ");
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    /**
     * 使用 sentinel 抛出异常的方式定义资源
     * 1), 代码的方式  try (Entry entry = SphU.entry("skuDetails")) { 业务逻辑 }catch(BlockingException e){}
     * 2), 基于注解的方式 @SentinelResource(blockHandler = "blockHandlerForGetUser")
     *
     * @param skuid
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @SentinelResource(value = "itemResources", blockHandler = "blockingHandler")
    @Override
    public ResponseItemSkuVo item(Long skuid) throws ExecutionException, InterruptedException {

        try (Entry entry = SphU.entry("skuDetails")) {
            ResponseItemSkuVo itemSkuVo = new ResponseItemSkuVo();

            CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
                //1 查询 sku 基本信息 pms_sku_info
                SkuInfoEntity infoEntity = this.getById(skuid);
                itemSkuVo.setSkuInfoEntity(infoEntity);
                return infoEntity;
            }, threadPoolExecutor);


            CompletableFuture<Void> wareskuFuture = infoFuture.thenAcceptAsync((res) -> {
                //设置是否有库存
                R stock = wareSkuFeignService.getSkuHasStock(Arrays.asList(res.getSkuId()));
                log.info("{} 的skuid 库存= {}", res.getSkuId(), stock.get("data"));
                List<ResponseSkuHasStockVo> stockData = stock.getData(new TypeReference<List<ResponseSkuHasStockVo>>() {
                });

                if (CollectionUtils.isEmpty(stockData)) {
                    itemSkuVo.setHasStock(false);
                } else {
                    itemSkuVo.setHasStock(stockData.get(0).getHasStock());
                }
            }, threadPoolExecutor);

            CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
                //3 获取spu的销售属性集合
                List<ResponseItemSkuVo.ItemSkuSaleAttrVo> itemSkuSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
                itemSkuVo.setSaleAttrVos(itemSkuSaleAttrVos);
            }, threadPoolExecutor);

            CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
                //4 获取spu的介绍 SpuInfoDescEntity
                SpuInfoDescEntity descServiceById = spuInfoDescService.getById(res.getSpuId());
                itemSkuVo.setSpuInfoDescEntity(descServiceById);
            }, threadPoolExecutor);

            CompletableFuture<Void> attrFuture = infoFuture.thenAcceptAsync((res) -> {
                //5 获取spu规格参数信息
                List<ResponseItemSkuVo.ItemSpuBaseAttrVo> itemSpuBaseAttrVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
                itemSkuVo.setItemSpuBaseAttrVos(itemSpuBaseAttrVos);
            }, threadPoolExecutor);

            CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
                //2 获取sku图片信息 pms_sku_images
                List<SkuImagesEntity> imagesEntities = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuid));
                itemSkuVo.setSkuImagesEntityList(imagesEntities);
            }, threadPoolExecutor);


            //远程查询 是否参与秒杀服务
            CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
                R buSkuId = seckillFeignService.getSeckillBuSkuId(skuid);
                log.info("商品详情页,远程调用秒杀服务的接口 如果为0表示成功 {}", buSkuId.get("code"));
                SeckillSkuRedisDetailsTo data = buSkuId.getData(new TypeReference<SeckillSkuRedisDetailsTo>() {
                });
                itemSkuVo.setSeckillSkuRedisDetailsTo(data);
            }, threadPoolExecutor);

            //等待所有任务完成
            CompletableFuture.allOf(saleAttrFuture, descFuture, attrFuture, imagesFuture, wareskuFuture, seckillFuture).get();

            return itemSkuVo;
        } catch (BlockException exception) {
            log.error("访问详情页面的数据收到sentinel限流 {}", exception.getMessage());
        }
        return null;
    }

    //blockHandler 函数，原方法调用被限流/降级/系统保护的时候调用
    public ResponseItemSkuVo blockingHandler(Long skuid,BlockException blockException) {
        log.error("item 方法被降级了 {}", blockException.getMessage());
        return null;
    }


    @Override
    public BigDecimal getPriceBySkuId(Long skuId) {
        return this.getById(skuId).getPrice();
    }
}