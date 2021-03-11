package com.atguigu.gulimail.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.feign.MemberFeignService;
import com.atguigu.gulimail.ware.vo.ResponseAddressAndFareVo;
import com.atguigu.gulimail.ware.vo.UserAddressVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.WareDao;
import com.atguigu.gulimail.ware.entity.WareEntity;
import com.atguigu.gulimail.ware.service.WareService;
import org.springframework.util.StringUtils;


@Slf4j
@Service("wareService")
public class WareServiceImpl extends ServiceImpl<WareDao, WareEntity> implements WareService {


    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("name", key);
            });
        }

        IPage<WareEntity> page = this.page(
                new Query<WareEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    @Override
    public ResponseAddressAndFareVo getFare(Long addrId) {
        R info = memberFeignService.getAddrInfo(addrId);
        log.info("调用远程的会员服务查新到当前的地址为 {}", info.get("userAddress"));
        ;
        UserAddressVo userAddress = JSON.parseObject(JSON.toJSONString(info.get("userAddress")), new TypeReference<UserAddressVo>() {
        });

        if (null != userAddress) {
            //调用第三方快递的接口 进行查询运费
            String price = userAddress.getPhone().substring(userAddress.getPhone().length() - 1, userAddress.getPhone().length());
            return new ResponseAddressAndFareVo(new BigDecimal(price), userAddress);
        }
        return null;
    }
}