package com.atguigu.gulimail.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimail.elasticsearch.GulimailElasticsearchMainApplication;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@Slf4j
@SpringBootTest(classes = GulimailElasticsearchMainApplication.class)
@RunWith(SpringRunner.class)
public class ESCURDTestCase {


    @Autowired
    private RestHighLevelClient esRestClient;

    @Test
    public void contextLoags() {
        Assert.assertNotNull(esRestClient);
    }

    /**
     * 简单添加一个索引 增加一个数据
     *
     * @throws IOException
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        //数据id
        indexRequest.id("1");
        indexRequest.source("userName", "zs", "age", 18, "gender", "男");
        IndexResponse response = esRestClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("response.status().getStatus() === {}", response.status().getStatus());
        log.info("response === {}", response);
    }

    /**
     * 按照年龄聚合,并且请求这些年龄段的这些人的平均薪资
     * 构建复杂的查询语句
     */
    @Test
    public void complexSelect() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //创建检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("address", "mill"));
        builder.from();
        builder.size();
        //按照年龄的值分布
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAggs").field("age").size(10);
        builder.aggregation(ageAgg);
        //计算平均薪资
        builder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        log.info(" SearchSourceBuilder {}", builder);
        searchRequest.indices("bank").source(builder);
        //结果分析
        SearchResponse response = esRestClient.search(searchRequest, RequestOptions.DEFAULT);

        //Map parseObject = JSON.parseObject(response.toString(), Map.class);
        //1 获取所有返回命中的记录
        SearchHits hits = response.getHits();
        //真正命中的记录
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit each : hits1) {
            String asString = each.getSourceAsString();
            //把字符串转换为 实体类
            AccountBean accountBean = JSON.parseObject(asString, AccountBean.class);
            System.out.println(accountBean);

        }
        //2 拿到这次检索的分析数据
        Aggregations aggregations = response.getAggregations();
        Terms ageAggs = aggregations.get("ageAggs");

        for (Terms.Bucket bucket : ageAggs.getBuckets()) {
            System.out.println("年龄" + bucket.getKeyAsString() +"   count " +bucket.getDocCount());
        }
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资===" + balanceAvg.getValue());
        log.info("response {}", response);

    }


    @Data
    @ToString
    static class AccountBean {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

}
