package com.atguigu.gulimail.cart.service;

import com.atguigu.gulimail.cart.vo.ShoppingCartVo;
import com.atguigu.gulimail.cart.vo.ShoppingItems;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ShoppingCartService {


    /**
     * 添加商品到购物车
     *
     * @param skuId skuid
     * @param num   商品数量
     * @return
     */
    ShoppingItems addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 根据skuid 查询出当前商品的详细信息
     *
     * @param skuId
     * @return
     */
    ShoppingItems getCartItemBySkuId(Long skuId);

    ShoppingCartVo getCartVo() throws ExecutionException, InterruptedException;


    /**
     * 根据key 进行清空购物车内容
     *
     * @param key
     */
    public void tmpCartRemoveByKey(String key);

    /**
     * 勾选购物项
     *
     * @param skuId
     * @param check
     */
    void checkCartItem(Long skuId, Integer check);

    /**
     * 购物车添加/去掉 商品的数量
     *
     * @param skuId
     * @param count
     */
    void countItemNum(Long skuId, Integer count);

    /**
     * 根据skuid 从购物车删除指定数据
     *
     * @param skuId
     */
    void deleteCartItemBySkuId(Long skuId);

    /**
     * 获取当前用户的全部购物车数据
     *
     * @return
     */
    List<ShoppingItems> getUserCartItems();
}
