package com.atguigu.gulimail.order.web;

import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;


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


}
