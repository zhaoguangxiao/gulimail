package com.atguigu.gulimail.product.web;

import com.atguigu.gulimail.product.service.SkuInfoService;
import com.atguigu.gulimail.product.vo.ResponseItemSkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku 详情
     *
     * @param skuid
     * @return
     */
    @GetMapping(value = "/{skuid}.html")
    public String skuItem(@PathVariable("skuid") Long skuid,
                          Model model) throws ExecutionException, InterruptedException {
        ResponseItemSkuVo responseItemSkuVo = skuInfoService.item(skuid);
        model.addAttribute("responseItemSkuVo", responseItemSkuVo);
        return "item";
    }
}
