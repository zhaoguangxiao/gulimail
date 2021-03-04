package com.atguigu.gulimail.thirdparty;

import com.atguigu.gulimail.thridparty.GuliMailThirdPartyApplicationMain3388;
import com.atguigu.gulimail.thridparty.component.SmsComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Pattern;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = GuliMailThirdPartyApplicationMain3388.class)
public class SendSMSTestCase {



    @Autowired
    private SmsComponent smsComponent;

    @Test
    public void sendSMS(){
        smsComponent.sendSms("17613720880","2525");
    }




}
