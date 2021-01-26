package com.atguigu.gulimail.thirdparty;

import com.aliyun.oss.OSSClient;
import com.atguigu.gulimail.thridparty.GuliMailThirdPartyApplicationMain3388;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GuliMailThirdPartyApplicationMain3388.class)
public class OssTestCase {

    @Autowired
    private OSSClient ossClient;

    @Test
    public void aliyunoss() throws FileNotFoundException {

        //上传文件流
        FileInputStream stream = new FileInputStream("C:\\Users\\Administrator\\Pictures\\lADPDgQ9rMzZLSdkZA_100_100.jpg");
        ossClient.putObject("gulimail-zgx","cat.jpg",stream);
        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成");
    }
}
