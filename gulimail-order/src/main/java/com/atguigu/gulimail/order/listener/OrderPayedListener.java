package com.atguigu.gulimail.order.listener;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimail.order.config.AlipayTemplate;
import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.PayAsyncVo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 处理支付宝支付成功改变订单状态
 */
@Slf4j
@RestController
public class OrderPayedListener {


    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;


    @PostMapping("/payed/notity")
    public String handlerAlipayed(PayAsyncVo payAsyncVo,
                                  HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        //验证签名 判断是不是支付宝给我们发送的

        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
           // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        Boolean alipayed = alipayTemplate.checkAlipayed(params);
        if (alipayed) {
            log.info("签名验证成功 {} 进行修改订单状态", alipayed);
            String result = orderService.handlerAlipayed(payAsyncVo);
            //只要收到了支付宝给我们的异步通知,告诉我们订单支付成功,我们返回success 支付宝就再也不通知了
            return result;
        }
        log.info("签名验证失败{}",alipayed);
        return "error";
    }


}
