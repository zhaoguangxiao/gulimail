package com.atguigu.gulimail.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.SkuInfoVo;
import com.atguigu.gulimail.cart.feign.SkuInfoFeignService;
import com.atguigu.gulimail.cart.interceptor.ShoppingCartInterceptor;
import com.atguigu.gulimail.cart.service.ShoppingCartService;
import com.atguigu.gulimail.cart.vo.ShoppingCartVo;
import com.atguigu.gulimail.cart.vo.ShoppingItems;
import com.atguigu.gulimail.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.ShoppingCartConstant.REDIS_CART_KEY_PREFIX;

@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SkuInfoFeignService skuInfoFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * @param skuId skuid
     * @param num   商品数量
     * @return
     */
    @Override
    public ShoppingItems addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //拿到需要操作的购物车
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        //判断商品是否已经存在购物车
        String cartEntity = (String) ops.get(skuId.toString());
        if (StringUtils.isEmpty(cartEntity)) {
            //添加新商品到购物车
            ShoppingItems shoppingItems = new ShoppingItems();
            //购物车 无此商品
            CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
                //1 远程查询sku信息
                //通过skuid 查询出当前商品的详细信息
                R skuInfoBySkuId = skuInfoFeignService.getSkuInfoBySkuId(skuId);
                log.info("当前加入购物车的商品详细信息为 {} skuid = {}", skuInfoBySkuId.get("skuInfo"), skuId);

                //把json 数据转化为对象
                SkuInfoVo skuInfo = JSON.parseObject(JSON.toJSONString(skuInfoBySkuId.get("skuInfo")), new TypeReference<SkuInfoVo>() {
                });
                shoppingItems.setSkuId(skuId);
                shoppingItems.setCheck(true);
                shoppingItems.setTitle(skuInfo.getSkuTitle());
                shoppingItems.setDefaultImage(skuInfo.getSkuDefaultImg());
                shoppingItems.setPrice(skuInfo.getPrice());
                shoppingItems.setCount(num);
            }, threadPoolExecutor);

            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = skuInfoFeignService.getSkuSaleAttrValues(skuId);
                log.info("远程调用 商品服务查询组合的销售属性为 {} skuid={}", skuSaleAttrValues, skuId);
                shoppingItems.setSkuAttr(skuSaleAttrValues);
            }, threadPoolExecutor);

            //等待全部定时任务完成 然后在返回
            CompletableFuture.allOf(runAsync, completableFuture).get();
            //保存redis
            ops.put(skuId.toString(), JSON.toJSONString(shoppingItems));
            return shoppingItems;
        } else {
            //购物车有此商品
            ShoppingItems items = JSON.parseObject(cartEntity, new TypeReference<ShoppingItems>() {
            });
            items.setCount(items.getCount() + num);
            //更新redis 数据
            ops.put(skuId.toString(), JSON.toJSONString(items));
            return items;
        }
    }

    /**
     * @return 返回操作的购物车
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //得到当前的用户信息
        UserInfoTo infoTo = ShoppingCartInterceptor.userInfoToThreadLocal.get();
        //1判断 用户是否登录 或者是临时登录
        String cartKey;
        if (null != infoTo.getUserId()) {
            //自己的购物车添加
            cartKey = REDIS_CART_KEY_PREFIX + infoTo.getUserId();
        } else {
            //临时购物车添加
            cartKey = REDIS_CART_KEY_PREFIX + infoTo.getUserKey();
        }

        //操作购物车
        //2 判断当前购物车是否存在 如果存在 数量+1 如果不存在新增一个商品
        //2-1
        return stringRedisTemplate.boundHashOps(cartKey); //绑定一个key 所有的增删改查操作
    }


    @Override
    public ShoppingItems getCartItemBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        Object o = cartOps.get(skuId.toString());
        return JSON.parseObject(o.toString(), ShoppingItems.class);
    }


    @Override
    public ShoppingCartVo getCartVo() throws ExecutionException, InterruptedException {

        ShoppingCartVo vo = new ShoppingCartVo();
        //判断用户是否登录
        UserInfoTo userInfoTo = ShoppingCartInterceptor.userInfoToThreadLocal.get();
        if (null == userInfoTo) {
            //未登录
            //1 获取临时购物车的全部内容
            List<ShoppingItems> items = getCartItems(REDIS_CART_KEY_PREFIX + userInfoTo.getUserKey());
            vo.setShoppingItems(items);
        } else {
            //登录 逻辑

            //1获取当前用户临时购物车内容
            String tmpkey = REDIS_CART_KEY_PREFIX + userInfoTo.getUserKey();
            List<ShoppingItems> tmpItems = getCartItems(tmpkey);
            if (!CollectionUtils.isEmpty(tmpItems)) {
                //临时购物车有内容 需要进行合并
                for (ShoppingItems items : tmpItems) {
                    //把临时商品添加进购物车
                    this.addToCart(items.getSkuId(), items.getCount());
                }

                //清空临时购物车内容
                tmpCartRemoveByKey(tmpkey);
            }

            //2获取当前用户的全部购物车内容 [包含合并过来的临时购物车数据,和登录后的全部购物车信息]
            List<ShoppingItems> userItems = getCartItems(REDIS_CART_KEY_PREFIX + userInfoTo.getUserId());
            vo.setShoppingItems(userItems);

        }
        return vo;
    }

    /**
     * 获取临时购物车内容
     *
     * @param key redis保存的主键
     * @return
     */
    private List<ShoppingItems> getCartItems(String key) {
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(key);
        if (!ops.values().isEmpty()) {
            List<ShoppingItems> shoppingItems = ops.values().stream().map(item -> {
                return JSON.parseObject(item.toString(), ShoppingItems.class);
            }).collect(Collectors.toList());
            return shoppingItems;
        }
        return null;
    }


    public void tmpCartRemoveByKey(String key) {
        //清空购物车内容
        stringRedisTemplate.delete(key);
    }

    @Override
    public void checkCartItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        ShoppingItems item = getCartItemBySkuId(skuId);
        item.setCheck(check == 1);
        cartOps.put(skuId.toString(), JSON.toJSONString(item));

    }


    @Override
    public void countItemNum(Long skuId, Integer count) {
        //拿到当前操作
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        ShoppingItems itemBySkuId = getCartItemBySkuId(skuId);
        itemBySkuId.setCount(count);
        cartOps.put(skuId.toString(), JSON.toJSONString(itemBySkuId));
    }

    @Override
    public void deleteCartItemBySkuId(Long skuId) {
        //拿到当前操作
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        ShoppingItems itemBySkuId = getCartItemBySkuId(skuId);
        if (null != itemBySkuId) {
            cartOps.delete(skuId.toString());
        }
    }
}
