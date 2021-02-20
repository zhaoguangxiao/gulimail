package com.atguigu.gulimail.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuESMode;
import com.atguigu.gulimail.elasticsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.atguigu.gulimail.elasticsearch.config.ElasticsearchConfig.COMMON_OPTIONS;
import static com.atguigu.gulimail.elasticsearch.constant.ESConstant.PRODUCT_INDEX;


@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean saveProductES(List<SkuESMode> skuESModes) throws IOException {
        //1 给ES 建立一个索引
        BulkRequest request = new BulkRequest();

        //构造请求参数
        for (SkuESMode item : skuESModes) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(PRODUCT_INDEX); //往哪个索引存数据
            indexRequest.id(item.getSkuId().toString()); // 设置id 因为skuid是唯一的
            String jsonString = JSON.toJSONString(item);
            indexRequest.source(jsonString, XContentType.JSON); //设置内容类型
            request.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(request, COMMON_OPTIONS);

        //todo 如果批量错误 可以在处理错误商品错误
        boolean hasFailures = bulk.hasFailures();

        List<Object> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());

        log.info(hasFailures + "商品上架 完成  {}", collect);
        return hasFailures;
    }


}
