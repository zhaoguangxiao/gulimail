package com.atguigu.gulimail.order.web;


import com.alipay.api.AlipayApiException;
import com.atguigu.gulimail.order.config.AlipayTemplate;
import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
public class PayWebController {


    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;


    /**
     * 1),将支付页面浏览器展示
     * 2), 支付成功后,我们要跳到用户订单页面
     *
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo vo = orderService.getOrderPay(orderSn);
        //支付宝返回的是一个页面 ,将此页面直接交给浏览器
        String pay = alipayTemplate.pay(vo);
        log.info("调用 AlipayTemplate 返回的信息为 {}", pay);
        return pay;
    }


}
