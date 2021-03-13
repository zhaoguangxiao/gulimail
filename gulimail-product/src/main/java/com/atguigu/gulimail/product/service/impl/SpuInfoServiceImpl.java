package com.atguigu.gulimail.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SkuBoundsTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.es.SkuESMode;
import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.product.dao.SpuInfoDao;
import com.atguigu.gulimail.product.entity.*;
import com.atguigu.gulimail.product.feign.ESSaveFeignService;
import com.atguigu.gulimail.product.feign.SkuBoundsFeignService;
import com.atguigu.gulimail.product.feign.WareSkuFeignService;
import com.atguigu.gulimail.product.service.*;
import com.atguigu.gulimail.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.ProductConstant.StatusEnum.UP_ENUM;


@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private ESSaveFeignService esSaveFeignService;

    @Autowired
    private WareSkuFeignService wareSkuFeignService;


    @Autowired
    private CategoryService categoryService;


    @Autowired
    private BrandService brandService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuBoundsFeignService skuBoundsFeignService;


    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * GlobalTransactional 适合使用分布式事务来保证事务的完成性
     * 这里不需要高并发
     *
     * @param spuSaveVo
     */
    @GlobalTransactional
    @Transactional
    @Override
    public void saveSpuSaveVo(SpuSaveVo spuSaveVo) {
        //1 保存spu 基本信息 --pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        //设置创建日期
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        //2 保存spu的描述信息 -- pms_spu_info_desc
        //2.1 获取图片集合
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity infoDescEntity = new SpuInfoDescEntity();
        infoDescEntity.setSpuId(spuInfoEntity.getId());
        infoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(infoDescEntity);
        //3 保存spu 图片集 -- pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
        //4 保存spu规格参数 -- pms_product_attr_value
        //获取所有的基本属性
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> valueEntities = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setSpuId(spuInfoEntity.getId());
            valueEntity.setAttrId(item.getAttrId());
            AttrEntity entity = attrService.getById(item.getAttrId());
            valueEntity.setAttrName(entity.getAttrName());
            valueEntity.setAttrValue(item.getAttrValues());
            valueEntity.setQuickShow(item.getShowDesc());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(valueEntities);


        // 5.4.1 保存商品积分信息
        SkuBoundsTo skuBoundsTo = new SkuBoundsTo();
        Bounds bounds = spuSaveVo.getBounds();
        BeanUtils.copyProperties(bounds, skuBoundsTo);
        skuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = skuBoundsFeignService.saveSkuBounds(skuBoundsTo);
        log.info("feign商品积分信息保存结果为 {}", r.get("code").toString());


        //保存积分信息 guli_sms库 --sms_sku_bounds
        List<Skus> skus = spuSaveVo.getSkus();
        //5 保存当前spu 对应的所有sku信息
        if (!CollectionUtils.isEmpty(skus)) {
            //获取图片集合
            skus.forEach(each -> {
                //获取当前默认图片
                String imagesUrl = null;
                for (Images item : each.getImages()) {
                    if (item.getDefaultImg() == 1) {
                        imagesUrl = item.getImgUrl();
                    }
                }
                SkuInfoEntity entity = new SkuInfoEntity();
                //skuName  price   skuTitle   skuSubtitle
                BeanUtils.copyProperties(each, entity);
                entity.setSpuId(spuInfoEntity.getId());
                entity.setBrandId(spuInfoEntity.getBrandId());
                entity.setCatalogId(spuInfoEntity.getCatalogId());
                entity.setSaleCount(0L);
                entity.setSkuDefaultImg(imagesUrl);

                //5.1 sku 基本信息,--  pms_sku_info
                //保存skuinfo 信息
                skuInfoService.save(entity);
                Long skuId = entity.getSkuId();
                //保存图片信息 skuinfoimages
                List<SkuImagesEntity> collect = each.getImages().stream().map(item -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    BeanUtils.copyProperties(item, skuImagesEntity);
                    skuImagesEntity.setSkuId(skuId);
                    return skuImagesEntity;
                }).filter(imagesEntity -> {
                    return !StringUtils.isEmpty(imagesEntity.getImgUrl());
                }).collect(Collectors.toList());

                //5.2 保存sku 的图片信息 -- pms_sku_images
                skuImagesService.saveBatch(collect);

                //5.3 保存sku的销售属性信息 --pms_sku_sale_attr_value
                List<Attr> attr = each.getAttr();
                List<SkuSaleAttrValueEntity> valueEntities1 = attr.stream().map(item -> {
                    SkuSaleAttrValueEntity valueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(item, valueEntity);
                    valueEntity.setSkuId(skuId);

                    return valueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(valueEntities1);


                SkuReductionTo reductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(each, reductionTo);
                reductionTo.setSkuId(skuId);
                if (reductionTo.getFullCount() > 0 || reductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
                    R r1 = skuBoundsFeignService.saveSkuReduction(reductionTo);
                    log.info("优惠满减信息保存,结果为{}", r1.get("code").toString());
                }
            });
        }

        //5.4 保存sku 的优惠满减信息 -- guli_sms库 --sms_sku_ladder || sms_sku_full_reduction || sms_member_price


    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        //status: 1
        //key:
        //brandId: 9
        //catelogId: 225

        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    @Override
    public void up(Long spuId) {
        //1 根据spuid 查出所有的sku信息,品牌名称
        List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);

        //查询当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> valueEntities = productAttrValueService.baseListForSpu(spuId);

        List<Long> attrIds = valueEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.getSearchAttrs(attrIds);
        Set<Long> idsSet = new HashSet<>(searchAttrIds);

        List<SkuESMode.Attrs> collect = valueEntities.stream().filter(item -> {
            return idsSet.contains(item.getAttrId());
        }).map(item -> {
            SkuESMode.Attrs attr = new SkuESMode.Attrs();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());


        //远程调用查看 是否存在库存
        List<Long> skuIds = skus.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());

        Map<Long, Boolean> booleanMap = null;
        try {
            R skuHasStock = wareSkuFeignService.getSkuHasStock(skuIds);
            log.info("远程调用库存系统 {}", skuHasStock);
            //把list 转换为 map 进行快速查找
            TypeReference<List<ResponseSkuHasStockVo>> typeReference = new TypeReference<List<ResponseSkuHasStockVo>>() {
            };
            booleanMap = skuHasStock.getData(typeReference).stream().collect(Collectors.toMap(ResponseSkuHasStockVo::getSkuId, ResponseSkuHasStockVo::getHasStock));
        } catch (Exception exception) {
            log.error("库存远程调用失败 {}", exception);
        }


        //封装sku信息
        Map<Long, Boolean> finalBooleanMap = booleanMap;
        List<SkuESMode> skuESModes = skus.stream().map(item -> {
            SkuESMode skuESMode = new SkuESMode();
            BeanUtils.copyProperties(item, skuESMode);
            skuESMode.setSkuPrice(item.getPrice());
            skuESMode.setSkuImg(item.getSkuDefaultImg());

            //查询出它的当前品牌
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            //查出它的当前分类
            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            //设置库存信息
            if (finalBooleanMap.isEmpty()) {
                skuESMode.setHasStock(true);
            } else {
                skuESMode.setHasStock(finalBooleanMap.get(item.getSkuId()));
            }

            //设置热度
            skuESMode.setHotScore(0L);
            //设置品牌名称
            skuESMode.setBrandName(brandEntity.getName());
            //设置品牌默认图片
            skuESMode.setBrandImg(brandEntity.getLogo());
            //设置分类名称
            skuESMode.setCatalogName(categoryEntity.getName());

            //设置检索属性
            skuESMode.setAttrs(collect);

            return skuESMode;
        }).collect(Collectors.toList());

        R statusUp = esSaveFeignService.productStatusUp(skuESModes);
        if (0 == (Integer) statusUp.get("code")) {
            //上传ES 成功 修改 商品状态
            baseMapper.updateStatusById(spuId, UP_ENUM.getCode());
        } else {
            //上传失败
            log.error("远程上传ES失败 {}", statusUp.get("code"));
            //todo 问题 重复调用问题?接口幂等性 重试机制问题?
            //1 构造请求数据,将对象转为json    RequestTemplate template = this.buildTemplateFromArgs.create(argv);
            //2 发送请求进行执行(执行成功会解码数据)  this.executeAndDecode(template, options)
            //3 执行请求会有重试机制
            //        while(true) {
            //            try {
            //                return this.executeAndDecode(template, options);
            //            } catch (RetryableException var9) {
        }
    }

    @Override
    public SpuInfoEntity getSpuEntityBySkuId(Long skuId) {
        SkuInfoEntity serviceById = skuInfoService.getById(skuId);
        return this.getById(serviceById.getSpuId());
    }
}