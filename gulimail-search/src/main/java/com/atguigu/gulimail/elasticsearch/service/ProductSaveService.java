package com.atguigu.gulimail.elasticsearch.service;

import com.atguigu.common.to.es.SkuESMode;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean saveProductES(List<SkuESMode> skuESModes) throws IOException;
}
