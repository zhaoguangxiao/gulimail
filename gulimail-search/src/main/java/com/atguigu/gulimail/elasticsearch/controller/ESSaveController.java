package com.atguigu.gulimail.elasticsearch.controller;


import com.atguigu.common.to.es.SkuESMode;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.elasticsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.atguigu.common.exception.BizCodeEnume.PRODUCT_UP_FAILED;

@Slf4j
@RestController
@RequestMapping(value = "/search")
public class ESSaveController {


    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping(value = "/product")
    public R productStatusUp(@RequestBody List<SkuESMode> skuESModes) {
        boolean saveProductES = false;
        try {
            saveProductES = productSaveService.saveProductES(skuESModes);
        } catch (IOException e) {
            log.error("ESSaveController 商品上架错误 {}", e);
            return R.error(PRODUCT_UP_FAILED.getCode(), PRODUCT_UP_FAILED.getMessage());
        }
        if (!saveProductES) {
            return R.ok();
        }
        return R.error(PRODUCT_UP_FAILED.getCode(), PRODUCT_UP_FAILED.getMessage());
    }
}
