package com.atguigu.gulimail.product.web;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.ResponseCategoryLog2Vo;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.feign.SeckillFeignService;
import com.atguigu.gulimail.product.service.CategoryService;
import com.atguigu.gulimail.product.vo.SeckillSkuRedisDetailsTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Slf4j
@Controller
public class IndexController {


    @Autowired
    private CategoryService categoryService;


    @Autowired
    private SeckillFeignService seckillFeignService;

    @GetMapping(value = {"/", "/index.html"})
    public String index(Model model) {
        //查出所有一级分类
        List<CategoryEntity> categoryEntityLists = categoryService.getLevelCategorys();
        model.addAttribute("categoryEntityLists", categoryEntityLists);

        //查出所有秒杀信息
        R currentSeckillSkus = seckillFeignService.getCurrentSeckillSkus();
        log.info("远程调用秒杀服务,查询当前时间的秒杀商品 {} 如果为0 表示查询成功", currentSeckillSkus.get("code"));
        List<SeckillSkuRedisDetailsTo> skusData = currentSeckillSkus.getData(new TypeReference<List<SeckillSkuRedisDetailsTo>>(){
        });
        model.addAttribute("seckills", skusData);
        return "index";
    }


    @ResponseBody
    @GetMapping(value = "/index/catelog.json")
    public Map<String, List<ResponseCategoryLog2Vo>> getCatelogJson() {
        return categoryService.getCatelogJson();
    }


}
