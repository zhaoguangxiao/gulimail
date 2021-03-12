package com.atguigu.gulimail.order.web;

import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.OrderConfirmVo;
import com.atguigu.gulimail.order.vo.OrderSubmitVo;
import com.atguigu.gulimail.order.vo.ResponseSubmitOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String submitOrder(OrderSubmitVo orderSubmitVo,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        ResponseSubmitOrderVo responseSubmitOrderVo = orderService.submitOrder(orderSubmitVo);
        if (responseSubmitOrderVo.getCode() == 0) {
            model.addAttribute("responseSubmitOrderVo", responseSubmitOrderVo);
            //跳转支付选择页
            return "pay";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("下单失败: ");
        switch (responseSubmitOrderVo.getCode()) {
            case 1:
                sb.append("订单已失效,请刷新后重试");
                break;
            case 2:
                sb.append("订单商品的价格发生后变化,请确认后重新提交");
                break;
            case 3:
                sb.append("商品没有库存了");
                break;
            default:
                sb.append("出现意料之外的问题");
        }
        redirectAttributes.addFlashAttribute("msg", sb.toString());
        //失败重新确认订单信息
        return "redirect:http://order.gulimail.com/toTrade";
    }

}
