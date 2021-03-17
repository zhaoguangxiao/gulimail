package com.atguigu.gulimail.seckill.scheduled;


import com.atguigu.gulimail.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 秒杀商品的定时上架
 * 每天晚上3点,上架最近三天需要秒杀的商品
 * 当天 00:00:00 -23:59:59
 * 明天 00:00:00 -23:59:59
 * 后天 00:00:00 -23:59:59
 */
@Slf4j
@Service
public class SeckillSkuScheduled {


    @Autowired
    private SeckillService seckillService;


    /**
     * 需要进行幂等性处理
     */
    @Scheduled(cron = "0 * * * * ?")
    public void uploadSeckillSkuLatest3Day() {
        log.info("正在准备上架秒杀商品...");
        //1 重复上架无需处理
        seckillService.uploadSeckillSkuLatest3Day();
    }

}
