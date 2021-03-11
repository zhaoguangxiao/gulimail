package com.atguigu.gulimail.order.web;

import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.OrderConfirmVo;
import com.atguigu.gulimail.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;


    /**
     * 跳转到确定订单页面
     *
     * @param model
     * @return
     */
    @GetMapping("/toTrade")
    public String orderConfirmPage(Model model) {
        OrderConfirmVo orderConfirmVo = null;
        try {
            orderConfirmVo = orderService.confirmOrder();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.addAttribute("orderConfirmVo", orderConfirmVo);
        return "confirm";
    }


    /**
     * 下单功能
     *
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo) {
        //去创建订单 ,验令牌 , 验价格 , 锁库存 ...

        //失败重新确认订单信息

        //跳转支付选择页
        return "";
    }

}
