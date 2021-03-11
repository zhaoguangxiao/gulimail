package com.atguigu.gulimail.ware.service;

import com.atguigu.gulimail.ware.vo.ResponseAddressAndFareVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.ware.entity.WareEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author guangxiaozhao
 * @email 1764773283@qq.com
 * @date 2021-01-21 09:46:44
 */
public interface WareService extends IService<WareEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据地址获取运费信息
     *
     * @param addrId
     * @return
     */
    ResponseAddressAndFareVo getFare(Long addrId);
}

