package com.atguigu.gulimail.elasticsearch.controller;

import com.atguigu.common.vo.ResponseThreeLeveVo;
import com.atguigu.gulimail.elasticsearch.feign.ProductFeognService;
import com.atguigu.gulimail.elasticsearch.service.MailSearchService;
import com.atguigu.gulimail.elasticsearch.service.ProductSaveService;
import com.atguigu.gulimail.elasticsearch.vo.RequestSearchParamVo;
import com.atguigu.gulimail.elasticsearch.vo.ResponseSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SearchController {


    @Autowired
    private MailSearchService mailSearchService;


    @Autowired
    private ProductSaveService productSaveService;


    @GetMapping(value = "list.html")
    public String listPage(RequestSearchParamVo requestSearchParamVo,
                           HttpServletRequest request,
                           Model model) {
        requestSearchParamVo.setQueryString(request.getQueryString());
        ResponseSearchVo responseSearchVo = mailSearchService.search(requestSearchParamVo);
        //保存搜索数据
        model.addAttribute("responseSearchVo", responseSearchVo);
        //查询全部分类数据
        List<ResponseThreeLeveVo> responseThreeLeveVoList = productSaveService.finCategoryLeveList();
        model.addAttribute("responseThreeLeveVoList", responseThreeLeveVoList);
        return "list";
    }

}
