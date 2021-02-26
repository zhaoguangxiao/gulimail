package com.atguigu.gulimail.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuESMode;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.elasticsearch.feign.ProductFeognService;
import com.atguigu.gulimail.elasticsearch.service.MailSearchService;
import com.atguigu.gulimail.elasticsearch.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.atguigu.gulimail.elasticsearch.config.ElasticsearchConfig.COMMON_OPTIONS;
import static com.atguigu.gulimail.elasticsearch.constant.ESConstant.*;

@Slf4j
@Service
public class MailSearchServiceImpl implements MailSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeognService productFeognService;


    /**
     * @param requestSearchParamVo 检索的所有参数
     * @return
     */
    @Override
    public ResponseSearchVo search(RequestSearchParamVo requestSearchParamVo) {
        ResponseSearchVo responseSearchVo = null;
        //1准备检索请求 --构建检索请求
        SearchRequest request = buildSearchRequest(requestSearchParamVo);

        try {
            //2执行检索请求
            SearchResponse searchResponse = restHighLevelClient.search(request, COMMON_OPTIONS);
            //3分析响应数据,分装成我们需要的数据
            responseSearchVo = buildSearchResult(searchResponse, requestSearchParamVo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseSearchVo;
    }

    /**
     * 构建检索请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(RequestSearchParamVo requestSearchParamVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //模糊匹配,过滤(按照属性,分类,品牌,价格区间,库存)
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String skuTitle = requestSearchParamVo.getKeyword();
        if (!StringUtils.isEmpty(skuTitle)) {
            //构建 match 按照名字和值进行匹配
            MatchQueryBuilder titleQueryBuilder = QueryBuilders.matchQuery("skuTitle", skuTitle);
            //must 模糊匹配
            boolQueryBuilder.must(titleQueryBuilder);
        }
        //构建filter --按照分类ID进行查找
        Long catalog3Id = requestSearchParamVo.getCatalog3Id();
        if (null != catalog3Id) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", catalog3Id));
        }
        //构建filter --按照多个品牌ID进行查询
        List<Long> brandId = requestSearchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }
        //构建filter --按照所有的属性进行查询
        List<String> attrs = requestSearchParamVo.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            //&attrs=1_5寸:8寸&attrs=2_15寸:18
            attrs.forEach(item -> {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                //attrs=1_5寸:8寸
                String[] strings = item.split("_");
                //检索的属性id
                String attrId = strings[0];
                //检索的属性值
                String[] attrValue = strings[1].split(":");
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                //每一个必须生成一个 nestedQuery
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            });
        }

        //构建filter 按照是否有库存进行查询
        //因为本身库存少 所以全部查询 requestSearchParamVo.getHasStock() == 0 ? true : false
        boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", false));

        //构建filter 按照价格区间进行查找
        String skuPrice = requestSearchParamVo.getSkuPrice();
        if (!StringUtils.isEmpty(skuPrice)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            //对skuPrice 进行截取得到 最小价格与最大价格
            String[] split = skuPrice.split("_");
            if (1 == split.length) {
                boolean starts = skuPrice.startsWith("_");
                if (starts) {
                    rangeQueryBuilder.lte(split[0]);//小于
                }
                boolean ends = skuPrice.endsWith("_");
                if (ends) {
                    rangeQueryBuilder.gte(split[0]);//大于
                }
            } else if (2 == split.length) {
                rangeQueryBuilder.gte(split[0]).lte(split[1]);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //把以前所有条件都拿来进行分装
        sourceBuilder.query(boolQueryBuilder);
        //排序,分页,高亮
        String sort = requestSearchParamVo.getSort();
        if (!StringUtils.isEmpty(sort)) {
            //sort=saleCount_asc/desc
            String[] split = sort.split("_");
            SortOrder sortOrder = split[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(split[0], sortOrder);
        }

        //分页
        Integer pageNum = requestSearchParamVo.getPageNum();
        if (null == pageNum || pageNum <= 0) { //如果pageNum <0 设置为第一页
            pageNum = 1;
        }
        sourceBuilder.from((pageNum - 1) * PRODUCT_PAGE_SIZE);
        sourceBuilder.size(PRODUCT_PAGE_SIZE);

        //高亮显示
        String paramVoKeyword = requestSearchParamVo.getKeyword();
        if (!StringUtils.isEmpty(paramVoKeyword)) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
            sourceBuilder.highlighter(builder);
        }

        //聚合分析
        TermsAggregationBuilder brand_aggs = AggregationBuilders.terms(PRODUCT_ATTR_BRAND_NAME).field("brandId").size(50);
        //子聚合
        brand_aggs.subAggregation(AggregationBuilders.terms(PRODUCT_ATTR_BRAND_NAME_AGGS).field("brandName").size(1));
        brand_aggs.subAggregation(AggregationBuilders.terms(PRODUCT_ATTR_BRAND_IMG_AGGS).field("brandImg").size(1));
        sourceBuilder.aggregation(brand_aggs);

        //分类聚合
        TermsAggregationBuilder catelogAggs = AggregationBuilders.terms(PRODUCT_ATTR_CATEGORY_TOTAL).field("catalogId").size(20);
        //分类子聚合
        catelogAggs.subAggregation(AggregationBuilders.terms(PRODUCT_ATTR_CATEGORY_NAME_AGGS).field("catalogName").size(1));
        sourceBuilder.aggregation(catelogAggs);


        //属性聚合
        NestedAggregationBuilder attrAggs = AggregationBuilders.nested(PRODUCT_ATTR_AGGS, "attrs");
        //嵌入聚合
        TermsAggregationBuilder attrIdAggs = AggregationBuilders.terms(PRODUCT_ATTR_ID_AGGS).field("attrs.attrId");
        //再次聚合
        attrIdAggs.subAggregation(AggregationBuilders.terms(PRODUCT_ATTR_NAME_AGGS).field("attrs.attrName").size(1));
        attrIdAggs.subAggregation(AggregationBuilders.terms(PRODUCT_ATTR_VALUE_AGGS).field("attrs.attrValue").size(50));
        attrAggs.subAggregation(attrIdAggs);
        sourceBuilder.aggregation(attrAggs);

        log.info("del 语句为: {}", sourceBuilder.toString());

        return new SearchRequest(new String[]{PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * 构建页面需要的结果数据
     *
     * @param searchResponse       查询es 返回的记录
     * @param requestSearchParamVo 页面请求实体类
     * @return
     */
    private ResponseSearchVo buildSearchResult(SearchResponse searchResponse, RequestSearchParamVo requestSearchParamVo) {
        ResponseSearchVo responseSearchVo = new ResponseSearchVo();

        //1 封装所有查询到的商品
        SearchHits hits = searchResponse.getHits(); //获取命中的记录
        Long total = hits.getTotalHits().value; //获取总记录数
        SearchHit[] searchHits = hits.getHits(); //拿到所有的命中记录
        List<SkuESMode> skuESModeList = null;
        if (null != searchHits && searchHits.length > 0) {
            skuESModeList = Arrays.asList(searchHits).stream().map(item -> {
                String sourceAsString = item.getSourceAsString(); //转为我们需要转换的数据
                SkuESMode skuESMode = JSON.parseObject(sourceAsString, SkuESMode.class);
                String keyword = requestSearchParamVo.getKeyword();
                if (!StringUtils.isEmpty(keyword)) {
                    HighlightField skuTitle = item.getHighlightFields().get("skuTitle");
                    String newSkuTitle = skuTitle.getFragments()[0].toString();
                    skuESMode.setSkuTitle(newSkuTitle);
                }
                return skuESMode;
            }).collect(Collectors.toList());
        }
        responseSearchVo.setProducts(skuESModeList);

        //2 封装当前所有商品涉及到的属性
        ParsedNested attrAggs = searchResponse.getAggregations().get(PRODUCT_ATTR_AGGS);
        ParsedLongTerms attrIdAggs = attrAggs.getAggregations().get(PRODUCT_ATTR_ID_AGGS);
        List<ResponseSearchVo.AttrVo> attrVos = attrIdAggs.getBuckets().stream().map(item -> {
            ResponseSearchVo.AttrVo attrVo = new ResponseSearchVo.AttrVo();
            attrVo.setAttrId((Long) item.getKey());
            ParsedStringTerms attrNameAggs = item.getAggregations().get(PRODUCT_ATTR_NAME_AGGS);
            attrVo.setAttrName(attrNameAggs.getBuckets().get(0).getKey().toString());

            ParsedStringTerms attrValueAggs = item.getAggregations().get(PRODUCT_ATTR_VALUE_AGGS);
            List<String> AttrVluesList = attrValueAggs.getBuckets().stream().map(each -> {
                return each.getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(AttrVluesList);
            return attrVo;
        }).collect(Collectors.toList());

        responseSearchVo.setAttrs(attrVos);


        //3 封装当前所有商品涉及到的品牌信息
        ParsedLongTerms aggregation = searchResponse.getAggregations().get(PRODUCT_ATTR_BRAND_NAME);
        List<ResponseSearchVo.BrandVo> brandCollections = aggregation.getBuckets().stream().map(item -> {
            ResponseSearchVo.BrandVo brandVo = new ResponseSearchVo.BrandVo();
            brandVo.setBrandId((Long) item.getKey()); //设置品牌id
            ParsedStringTerms brandNameAggs = item.getAggregations().get(PRODUCT_ATTR_BRAND_NAME_AGGS);
            brandVo.setBrandName(brandNameAggs.getBuckets().get(0).getKey().toString()); //设置品牌名称
            ParsedStringTerms brandImagesAggs = item.getAggregations().get(PRODUCT_ATTR_BRAND_IMG_AGGS);
            brandVo.setBrandImg(brandImagesAggs.getBuckets().get(0).getKey().toString()); //设置品牌图片路径
            return brandVo;
        }).collect(Collectors.toList());
        responseSearchVo.setBrands(brandCollections);

        //4 封装当前所有商品涉及到的分类信息
        Aggregations aggregations = searchResponse.getAggregations(); //拿到所有的聚合信息
        ParsedLongTerms catelog_aggs = aggregations.get(PRODUCT_ATTR_CATEGORY_TOTAL);
        List<ResponseSearchVo.CatelogVo> catelogVos = catelog_aggs.getBuckets().stream().map(item -> {
            ResponseSearchVo.CatelogVo catelogVo = new ResponseSearchVo.CatelogVo();
            catelogVo.setCatalogId((Long) item.getKey());
            //获取子聚合
            ParsedStringTerms itemAggregations = item.getAggregations().get(PRODUCT_ATTR_CATEGORY_NAME_AGGS);
            catelogVo.setCatalogName(itemAggregations.getBuckets().get(0).getKey().toString()); //设置分类名称
            return catelogVo;
        }).collect(Collectors.toList());

        responseSearchVo.setCatelogs(catelogVos);//设置分类信息


        //4 分页封装
        responseSearchVo.setTotal(total);  //设置总记录数
        int surplus = (int) (total % PRODUCT_PAGE_SIZE);
        int division = (int) (total / PRODUCT_PAGE_SIZE);
        Integer totalPage = 0 == surplus ? division : division + 1;
        responseSearchVo.setTotalPages(totalPage);  //设置总页码
        Integer voPageNum = requestSearchParamVo.getPageNum();
        responseSearchVo.setPageNum(null == voPageNum ? 1 : voPageNum); //设置页码 如果null 为当页码默认为第一页


        //5 保存搜索关键字
        responseSearchVo.setSearchKeyWord(requestSearchParamVo.getKeyword());


        //构建面包屑导航功能
        if (!CollectionUtils.isEmpty(requestSearchParamVo.getAttrs())) {
            List<Long> attrIds = responseSearchVo.getAttrIds();
            List<ResponseSearchVo.NavsVo> navsVos = requestSearchParamVo.getAttrs().stream().map(item -> {
                ResponseSearchVo.NavsVo navsVo = new ResponseSearchVo.NavsVo();
                //2_15寸
                String[] split = item.split("_");
                navsVo.setNavValue(split[1]);
                //拿到属性id
                Long attrId = Long.valueOf(split[0]);
                //远程调用
                R info = productFeognService.attrInfo(attrId);
                log.info("远程调用product-attr 服务结果为 {}", info.get("attr"));
                ResponseAttrVo attrVo = JSON.parseObject(JSON.toJSONString(info.get("attr")), new TypeReference<ResponseAttrVo>() {
                });
                navsVo.setNavName(attrVo.getAttrName());
                //拿到所有查询条件,去掉当前
                //attrs=15_以官网信息为准
                String newUrl = replaceQueryString("attrs", item, requestSearchParamVo.getQueryString());
                navsVo.setNavHref(newUrl);//设置跳转地方

                attrIds.add(attrId);//把请求参数分析添加进集合
                return navsVo;
            }).collect(Collectors.toList());
            responseSearchVo.setNavs(navsVos);
        }

        //品牌添加到面包屑导航
        List<Long> brandIds = requestSearchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandIds)) {
            //拿到面包屑导航
            List<ResponseSearchVo.NavsVo> navs = responseSearchVo.getNavs();
            ResponseSearchVo.NavsVo vo = new ResponseSearchVo.NavsVo();
            vo.setNavName("品牌");
            //远程查询
            R idSInfo = productFeognService.brandIdSInfo(brandIds);
            log.info("远程调用商品服务获取brandName 结果为{}", idSInfo.get("brand"));
            List<ResponseBrandVo> brandVos = JSON.parseObject(JSON.toJSONString(idSInfo.get("brand")), new TypeReference<List<ResponseBrandVo>>() {
            });
            if (!CollectionUtils.isEmpty(brandVos)) {
                StringBuffer sb = new StringBuffer();
                String newUrl = null;
                for (ResponseBrandVo item : brandVos) {
                    sb.append(item.getName());
                    //替换
                    newUrl = replaceQueryString("brandId", item.getBrandId().toString(), requestSearchParamVo.getQueryString());
                }
                vo.setNavValue(sb.toString());
                vo.setNavHref(newUrl);
            }
            navs.add(vo);
        }

        //将分类添加到面包屑
        Long catalog3Id = requestSearchParamVo.getCatalog3Id();
        if (null != catalog3Id) {
            List<ResponseSearchVo.NavsVo> navs = responseSearchVo.getNavs();
            ResponseSearchVo.NavsVo vo = new ResponseSearchVo.NavsVo();
            vo.setNavName("分类");
            R info = productFeognService.categoryInfo(catalog3Id);
            log.info("远程调用商品微服务查询的分类结果为:{}", info.get("category"));
            ResponseCategoryVo categoryVo = JSON.parseObject(JSON.toJSONString(info.get("category")), new TypeReference<ResponseCategoryVo>() {
            });


            vo.setNavValue(categoryVo.getName());
            String queryStringUrl = replaceQueryString("catalog3Id", catalog3Id.toString(), requestSearchParamVo.getQueryString());
            vo.setNavHref(queryStringUrl);
            navs.add(vo);
        }

        return responseSearchVo;
    }

    /**
     * @param result 需要截取的字符串
     * @param spit   以什么类型结尾的
     * @return 返回的结果
     */
    private String stringEndWith(String result, String spit) {
        if (!StringUtils.isEmpty(result) && result.endsWith(spit)) {
            //截取最后的 &符号
            return result.subSequence(0, result.length() - 1).toString();
        }
        return result;
    }

    /**
     * @param key     截取的key
     * @param thisVal 当前需要转换的值
     * @param replVal 需要替换的值
     * @return
     */
    private String replaceQueryString(String key, String thisVal, String replVal) {
        String navHrefUrl = null;
        try {
            //对浏览器空格编码和java不一样
            String encode = URLEncoder.encode(thisVal, "utf-8").replace("+", "%20");
            String queryString = replVal.replace(key + "=" + encode, "");
            //截取最后一个字符
            queryString = stringEndWith(queryString, "&");
            navHrefUrl = SEARCH_ROOT_URL + queryString;
            //截取最后一个字符
            navHrefUrl = stringEndWith(navHrefUrl, "?");
        } catch (UnsupportedEncodingException e) {
            log.error("对浏览器query进行编码转换异常{}", e.getMessage());
        }
        return navHrefUrl;
    }

}
