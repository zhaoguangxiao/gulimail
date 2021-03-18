package com.atguigu.gulimail.seckill.vo;


import lombok.Data;

/**
 * 详情页面发立即秒杀的数据接收类
 */
@Data
public class RequestSeckillVo {

    private String killId; //活动场次_商品skuid
    private String randomCode; //随机码
    private Integer num; //数量

}
