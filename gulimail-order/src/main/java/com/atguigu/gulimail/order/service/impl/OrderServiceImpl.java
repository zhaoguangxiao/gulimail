package com.atguigu.gulimail.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.LoginUserVo;
import com.atguigu.gulimail.order.feign.ShoppingCartFeignService;
import com.atguigu.gulimail.order.feign.UserAddressFeignService;
import com.atguigu.gulimail.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimail.order.vo.OrderConfirmVo;
import com.atguigu.gulimail.order.vo.OrderItemVo;
import com.atguigu.gulimail.order.vo.UserAddressVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.order.dao.OrderDao;
import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.service.OrderService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private UserAddressFeignService userAddressFeignService;


    @Autowired
    private ShoppingCartFeignService shoppingCartFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo vo = new OrderConfirmVo();
        //获取当前登录用户
        LoginUserVo vo1 = LoginUserInterceptor.threadLocal.get();
        //当前上下文请求属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> userAddress = CompletableFuture.runAsync(() -> {
            //设置异步线程头信息
            RequestContextHolder.setRequestAttributes(attributes);
            R data = userAddressFeignService.userAddressByUserId(vo1.getId());
            log.info("远程查询收货地址信息为 {}", data.get("data"));
            List<UserAddressVo> userAddressVo = JSON.parseObject(JSON.toJSONString(data.get("data")), new TypeReference<List<UserAddressVo>>() {
            });
            vo.setUserAddressVoList(userAddressVo);
        }, threadPoolExecutor);

        CompletableFuture<Void> cartItem = CompletableFuture.runAsync(() -> {
            //设置异步线程头信息
            RequestContextHolder.setRequestAttributes(attributes);
            //获取当前用户全部已勾选购物项
            R cartItems = shoppingCartFeignService.currentUserCartItems();
            //feign 在远程调用之前会构造请求,会调用很多的拦截器---feign 远程调用,这个请求没有任何请求头,新请求头 ---设置feign 远程请求拦截器
            log.info("远程调用购物车服务查询全部购物项 {}", cartItems.get("data"));
            List<OrderItemVo> orderItemVos = JSON.parseObject(JSON.toJSONString(cartItems.get("data")), new TypeReference<List<OrderItemVo>>() {
            });
            //设置选中的购物项
            vo.setOrderItemVos(orderItemVos);
        }, threadPoolExecutor);

        //设置用户积分信息
        vo.setIntegration(vo1.getIntegration());
        //其它数据自动计算
        //todo 设置防重令牌

        //阻塞等待全部异步任务完成然后进行下一次
        CompletableFuture.allOf(userAddress, cartItem).get();
        return vo;
    }
}