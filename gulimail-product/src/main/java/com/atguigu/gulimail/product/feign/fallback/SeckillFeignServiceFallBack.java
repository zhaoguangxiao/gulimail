package com.atguigu.gulimail.product.feign.fallback;

import com.atguigu.common.utils.R;
import com.atguigu.gulimail.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.atguigu.common.exception.BizCodeEnume.REQUEST_FLOW_MAX_EXCEPTION;


@Slf4j
@Component //添加进容器
public class SeckillFeignServiceFallBack implements SeckillFeignService {

    @Override
    public R getCurrentSeckillSkus() {
        return R.error(REQUEST_FLOW_MAX_EXCEPTION.getCode(), REQUEST_FLOW_MAX_EXCEPTION.getMessage());
    }

    @Override
    public R getSeckillBuSkuId(Long skuId) {
        log.info("执行了熔断方法 {}", skuId);
        return R.error(REQUEST_FLOW_MAX_EXCEPTION.getCode(), REQUEST_FLOW_MAX_EXCEPTION.getMessage());
    }
}
