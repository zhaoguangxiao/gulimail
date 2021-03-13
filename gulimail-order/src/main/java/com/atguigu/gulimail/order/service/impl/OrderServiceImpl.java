package com.atguigu.gulimail.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.ware.ResponseSkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.LoginUserVo;
import com.atguigu.gulimail.order.dao.OrderDao;
import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.entity.OrderItemEntity;
import com.atguigu.gulimail.order.enume.OrderStatusEnum;
import com.atguigu.gulimail.order.exception.NoStockException;
import com.atguigu.gulimail.order.feign.ProductFeignService;
import com.atguigu.gulimail.order.feign.ShoppingCartFeignService;
import com.atguigu.gulimail.order.feign.UserAddressFeignService;
import com.atguigu.gulimail.order.feign.WareFeignService;
import com.atguigu.gulimail.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimail.order.service.OrderItemService;
import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.*;
import com.atguigu.gulimail.order.to.CreateOrderTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.OrderCartConstant.USER_REDIS_ORDER_TOKEN_PREFIX;
import static com.atguigu.gulimail.order.entity.OrderEntity.DELETE_STATUS_NDELETED;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();


    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WareFeignService wareFeignService;

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
        }, threadPoolExecutor).thenRunAsync(() -> {
            //异步查询库存
            List<Long> skuIds = vo.getOrderItemVos().stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R hasStock = wareFeignService.getSkuHasStock(skuIds);
            log.info("远程查询 是否存在库存 {}", hasStock.get("code"));
            List<ResponseSkuHasStockVo> responseSkuHasStockVos = hasStock.getData(new TypeReference<List<ResponseSkuHasStockVo>>() {
            });
            if (!CollectionUtils.isEmpty(responseSkuHasStockVos)) {
                Map<Long, Boolean> map = responseSkuHasStockVos.stream().collect(Collectors.toMap(ResponseSkuHasStockVo::getSkuId, ResponseSkuHasStockVo::getHasStock));
                vo.setStocks(map);
            }
        }, threadPoolExecutor);


        //设置用户积分信息
        vo.setIntegration(vo1.getIntegration());
        //其它数据自动计算
        //设置防重令牌
        String uuId = IdUtil.simpleUUID();
        //页面保存令牌
        vo.setOrderToken(uuId);
        //服务端保存令牌 --设置令牌30分钟后过期
        stringRedisTemplate.opsForValue().set(USER_REDIS_ORDER_TOKEN_PREFIX + vo1.getId(), uuId, 30, TimeUnit.MINUTES);

        //阻塞等待全部异步任务完成然后进行下一次
        CompletableFuture.allOf(userAddress, cartItem).get();
        return vo;
    }


    /**
     * 验令牌[原子操作],去创建订单  , 验价格 , 锁库存 ...
     * Transactional 本地事务,在分布式系统下只能控制自己回滚,控制不了其它服务的回滚,
     * 分布式事务-能产生分布式事务的最大原因是 网络原因
     *
     * @param orderSubmitVo
     * @return
     */
    @GlobalTransactional
    @Transactional
    @Override
    public ResponseSubmitOrderVo submitOrder(OrderSubmitVo orderSubmitVo) {
        orderSubmitVoThreadLocal.set(orderSubmitVo);
        ResponseSubmitOrderVo orderVo = new ResponseSubmitOrderVo();
        //默认设置为0 只要有异常就会修改为其他值
        orderVo.setCode(0);
        //1 验证令牌 ---核心[验证令牌对比和删除必须是原子性操作]--执行脚本
        //获取当前登录用户
        LoginUserVo vo1 = LoginUserInterceptor.threadLocal.get();
        //0 代表删除失败 1代表删除成功
        Long execute = stringRedisTemplate.execute(new DefaultRedisScript<>("if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end", Long.class), Arrays.asList(USER_REDIS_ORDER_TOKEN_PREFIX + vo1.getId()), orderSubmitVo.getOrderToken());
        if (execute == 1) {
            //令牌验证成功
            CreateOrderTo order = createOrder();
            //进行验价格
            //应付金额
            BigDecimal payAmount = order.getOrderEntity().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            //验价格 [服务端查询的应付价格-前端的应付价格 在0.01 之间没问题]
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //保存订单
                saveOrder(order);
                //4 锁库存 --只要有异常 ,回滚订单数据
                WareSkuLockVo lockVo = new WareSkuLockVo();
                //设置订单id
                lockVo.setOrderSn(order.getOrderEntity().getOrderSn());
                List<OrderItemVo> itemVos = order.getItemVos().stream().map(item -> {
                    OrderItemVo vo = new OrderItemVo();
                    vo.setSkuId(item.getSkuId());
                    vo.setCount(item.getSkuQuantity());
                    vo.setTitle(item.getSkuName());
                    return vo;
                }).collect(Collectors.toList());
                lockVo.setOrderItemVoList(itemVos);
                R stockLocks = wareFeignService.orderStockLocks(lockVo);
                int i = 10 / 0;
                log.info("确定订单远程锁库存服务 {}", Integer.parseInt(stockLocks.get("code").toString()));
                if (Integer.parseInt(stockLocks.get("code").toString()) == 0) {
                    //锁定成功
                    //远程扣减 积分
                    return orderVo;
                } else {
                    //锁定失败了 库存锁定失败
                    orderVo.setCode(3);
                    String msg = (String) stockLocks.get("msg");
                    throw new NoStockException(msg);
                }
            } else {
                //金额对比失败
                orderVo.setCode(2);
            }
        } else {
            //令牌验证失败
            orderVo.setCode(1);
        }

        //令牌验证失败
        return orderVo;
    }

    /**
     * 保存订单数据
     *
     * @param order
     */
    private void saveOrder(CreateOrderTo order) {
        //保存订单
        OrderEntity entity = order.getOrderEntity();
        //创建时间
        entity.setCreateTime(new Date());
        this.save(entity);
        //保存订单项
        List<OrderItemEntity> itemVos = order.getItemVos();
        orderItemService.saveBatch(itemVos);
    }


    /**
     * 创建订单
     */
    private CreateOrderTo createOrder() {
        //页面传递的数据
        OrderSubmitVo vo = orderSubmitVoThreadLocal.get();
        CreateOrderTo orderTo = new CreateOrderTo();
        //商品id
        String timeId = IdWorker.getTimeId();
        //1 -构建订单
        OrderEntity orderEntity = buildOrderEntity(vo, timeId);
        //2 -获取所有订单项
        List<OrderItemEntity> orderItems = buildOrderItems(timeId);
        //3 -验价格[计算价格相关]
        checkPrice(orderEntity, orderItems);

        orderTo.setOrderEntity(orderEntity);
        orderTo.setItemVos(orderItems);
        return orderTo;
    }

    private void checkPrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal decimal = new BigDecimal("0.0");
        //1 订单价格相关的
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        //积分 -成长值
        BigDecimal growth = new BigDecimal("0.0");
        BigDecimal giftIntegration = new BigDecimal("0.0");
        for (OrderItemEntity orderItem : orderItems) {
            decimal = decimal.add(orderItem.getRealAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            integration = integration.add(orderItem.getIntegrationAmount());
            coupon = coupon.add(orderItem.getCouponAmount());
            growth = growth.add(new BigDecimal(orderItem.getGiftGrowth()));
            giftIntegration = giftIntegration.add(new BigDecimal(orderItem.getGiftIntegration()));
        }
        //订单总额
        orderEntity.setTotalAmount(decimal);
        //应付总额
        orderEntity.setPayAmount(decimal.add(orderEntity.getFreightAmount()));
        //优惠信息设置
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);
        //自动确认收货
        orderEntity.setAutoConfirmDay(7);
        //积分 成长值
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setIntegration(giftIntegration.intValue());
        //设置订单删除状态
        orderEntity.setDeleteStatus(DELETE_STATUS_NDELETED);
    }

    private List<OrderItemEntity> buildOrderItems(String timeId) {
        //最后确定每一次购物项的价格
        R items = shoppingCartFeignService.currentUserCartItems();
        log.info("创建订单远程查询购物项信息 {}", items.get("data"));
        List<OrderItemVo> cartLists = items.getData(new TypeReference<List<OrderItemVo>>() {
        });
        if (!CollectionUtils.isEmpty(cartLists)) {
            return cartLists.stream().map(item -> {
                OrderItemEntity itemEntity = buildOrderItem(item);
                itemEntity.setOrderSn(timeId);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private OrderEntity buildOrderEntity(OrderSubmitVo vo, String timeId) {
        LoginUserVo userVo = LoginUserInterceptor.threadLocal.get();
        OrderEntity entity = new OrderEntity();
        //构建用户信息
        entity.setUserId(userVo.getId());
        entity.setUsername(userVo.getUsername());
        //设置订单号
        entity.setOrderSn(timeId);
        //设置订单状态
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //获取收货地址信息
        R r = wareFeignService.getFareAndAddress(vo.getAddressId());
        log.info("创建订单,远程查询地址信息为 {}", r.get("data"));
        ResponseAddressAndFareVo address = r.getData(new TypeReference<ResponseAddressAndFareVo>() {
        });
        //1 -设置运费信息
        entity.setFreightAmount(address.getFare());
        //设置省份/直辖市
        entity.setReceiverProvince(address.getUserAddressVo().getProvince());
        //城市
        entity.setReceiverCity(address.getUserAddressVo().getCity());
        //区
        entity.setReceiverRegion(address.getUserAddressVo().getRegion());
        //详细地址
        entity.setReceiverAddress(address.getUserAddressVo().getAddress());
        //收货人姓名
        entity.setReceiverName(address.getUserAddressVo().getName());
        //收货人电话
        entity.setReceiverPhone(address.getUserAddressVo().getPhone());
        //收货人邮编
        entity.setReceiverPostCode(address.getUserAddressVo().getPostCode());
        return entity;
    }

    /**
     * 构建订单项
     *
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity entity = new OrderItemEntity();
        //1 -订单信息

        //2 -spu信息
        R bySpus = productFeignService.getSpuEntityBySkuId(item.getSkuId());
        log.info("创建订单远程查询 spu详细信息为 {}", bySpus.get("data"));
        SpuInfoVo spuInfoVo = bySpus.getData(new TypeReference<SpuInfoVo>() {
        });
        entity.setSpuId(spuInfoVo.getId());
        entity.setSpuBrand(spuInfoVo.getBrandId().toString());
        entity.setSpuName(spuInfoVo.getSpuName());
        entity.setCategoryId(spuInfoVo.getCatalogId());

        //3 -sku信息
        entity.setSkuId(item.getSkuId());
        entity.setSkuName(item.getTitle());
        entity.setSkuPic(item.getDefaultImage());
        entity.setSkuPrice(item.getPrice());
        //将集合变为 string
        List<String> skuAttr = item.getSkuAttr();
        String attrs = StringUtils.collectionToDelimitedString(skuAttr, ";");
        entity.setSkuAttrsVals(attrs);

        entity.setSkuQuantity(item.getCount());
        //4 -优惠信息

        //5 -积分信息
        int integral = item.getPrice().multiply(new BigDecimal(item.getCount())).intValue();
        entity.setGiftGrowth(integral);
        entity.setGiftIntegration(integral);

        //6 -订单项的价格设置
        entity.setPromotionAmount(new BigDecimal("0.0"));
        entity.setCouponAmount(new BigDecimal("0.0"));
        entity.setIntegrationAmount(new BigDecimal("0.0"));
        //当前订单项的总金额
        BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(item.getCount()));
        //减去优惠后的金额
        BigDecimal subtract = totalPrice.subtract(entity.getPromotionAmount()).subtract(entity.getCouponAmount()).subtract(entity.getIntegrationAmount());
        entity.setRealAmount(subtract);
        return entity;
    }
}