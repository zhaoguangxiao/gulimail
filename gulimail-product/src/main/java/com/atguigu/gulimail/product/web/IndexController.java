package com.atguigu.gulimail.product.web;

import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {


    @Autowired
    private CategoryService categoryService;


    @GetMapping(value = {"/", "/index.html"})
    public String index(Model model) {
        //查出所有一级分类
        List<CategoryEntity> categoryEntityLists= categoryService.getLevelCategorys();
        model.addAttribute("categoryEntityLists",categoryEntityLists);
        return "index";
    }


    @ResponseBody
    @GetMapping(value = "/index/catelog.json")
    public Map<String,List<ResponseCategoryLog2Vo>> getCatelogJson(){
       return categoryService.getCatelogJson();
    }


}
