package com.atguigu.gulimail.seckill.controller;


import com.atguigu.common.utils.R;
import com.atguigu.gulimail.seckill.service.SeckillService;
import com.atguigu.gulimail.seckill.to.SeckillSkuRedisDetailsTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@RestController
public class SeckillController {


    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与秒杀的商品信息
     *
     * @return
     */
    @GetMapping(value = "/currentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisDetailsTo> seckillSkuRedisDetailsTos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(seckillSkuRedisDetailsTos);
    }

}
