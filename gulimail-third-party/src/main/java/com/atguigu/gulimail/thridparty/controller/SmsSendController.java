package com.atguigu.gulimail.thridparty.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimail.thridparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/sms")
@RestController
public class SmsSendController {


    @Autowired
    private SmsComponent smsComponent;


    @GetMapping(value = "sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendSms(phone, code);
        return R.ok();
    }


}
