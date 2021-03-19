package com.atguigu.gulimail.seckill.controller;


import com.atguigu.common.utils.R;
import com.atguigu.gulimail.seckill.service.SeckillService;
import com.atguigu.gulimail.seckill.to.SeckillSkuRedisDetailsTo;
import com.atguigu.gulimail.seckill.vo.RequestSeckillVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@Controller
public class SeckillController {


    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与秒杀的商品信息
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/currentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisDetailsTo> seckillSkuRedisDetailsTos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(seckillSkuRedisDetailsTos);
    }


    /**
     * 根据sku 查询是否有活动场次
     *
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/seckill/{skuId}")
    public R getSeckillBuSkuId(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisDetailsTo seckillSkuRedisDetailsTo = seckillService.getSeckillBuSkuId(skuId);
        return R.ok().setData(seckillSkuRedisDetailsTo);
    }


    @GetMapping(value = "/checkseckill")
    public String checkSeckill(RequestSeckillVo requestSeckillVo,
                               Model model) {
        //1 自己的秒杀服务判断是否登录 使用 LoginInterceptor 过滤登录用户信息
        String orderSn = seckillService.checkSeckill(requestSeckillVo);
        model.addAttribute("orderSn", orderSn);
        return "success";//加入购物车成功页面
    }


}
