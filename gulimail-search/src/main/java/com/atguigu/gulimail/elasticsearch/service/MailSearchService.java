package com.atguigu.gulimail.elasticsearch.service;

import com.atguigu.gulimail.elasticsearch.vo.RequestSearchParamVo;
import com.atguigu.gulimail.elasticsearch.vo.ResponseSearchVo;

public interface MailSearchService {

    /**
     * @param requestSearchParamVo 检索的所有参数
     * @return 检索的所有结果
     */
    ResponseSearchVo search(RequestSearchParamVo requestSearchParamVo);

}
