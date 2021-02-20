package com.atguigu.gulimail.product.feign;

import com.atguigu.common.to.es.SkuESMode;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "gulimail-elasticsearch")
public interface ESSaveFeignService {

    @PostMapping(value = "/search/product")
    public R productStatusUp(@RequestBody List<SkuESMode> skuESModes);
}
