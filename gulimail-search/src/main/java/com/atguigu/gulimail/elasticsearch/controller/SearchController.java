package com.atguigu.gulimail.elasticsearch.controller;

import com.atguigu.gulimail.elasticsearch.service.MailSearchService;
import com.atguigu.gulimail.elasticsearch.vo.RequestSearchParamVo;
import com.atguigu.gulimail.elasticsearch.vo.ResponseSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {


    @Autowired
    private MailSearchService mailSearchService;


    @GetMapping(value = "list.html")
    public String listPage(RequestSearchParamVo requestSearchParamVo, Model model) {
        ResponseSearchVo responseSearchVo = mailSearchService.search(requestSearchParamVo);
        model.addAttribute("responseSearchVo", responseSearchVo);
        return "list";
    }
}
