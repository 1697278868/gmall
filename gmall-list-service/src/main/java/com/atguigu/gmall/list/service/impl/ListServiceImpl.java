package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.es.SkuLsInfo;
import com.atguigu.gmall.bean.es.SkuLsParams;
import com.atguigu.gmall.bean.es.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService{

    @Autowired
    JestClient jestClient;

    public static final String ES_INDEX="gmall_sku_info";

    public static final String ES_TYPE="doc";


    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        Index.Builder builder = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId());
        Index index = builder.build();
        try {
            DocumentResult documentResult = jestClient.execute(index);
            System.out.println("插入documentResult:"+documentResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult searchES(SkuLsParams skuLsParams) {

        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SkuLsResult skuLsResult = new SkuLsResult();
        try {
            SearchResult searchResult = jestClient.execute(search);

            List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
            //设置返回结果的skuLsInfoList
            List<SkuLsInfo> skuLsInfoList = new ArrayList<>();
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                if(hit.highlight!=null&&hit.highlight.size()>0) {
                    String skuName = hit.highlight.get("skuName").get(0);
                    source.setSkuName(skuName);

                }
                skuLsInfoList.add(source);
            }
            skuLsResult.setSkuLsInfoList(skuLsInfoList);
            //总记录数
            skuLsResult.setTotal(searchResult.getTotal());
            //总页数
            long totalPages = (searchResult.getTotal() + (skuLsParams.getPageSize() - 1))/ skuLsParams.getPageSize();
            skuLsResult.setTotalPages(totalPages);
            //属性值id
            List<String> attrValueIdList = new ArrayList<>();
            MetricAggregation aggregations = searchResult.getAggregations();
            List<TermsAggregation.Entry> buckets = aggregations.getTermsAggregation("groupby_attr").getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                String key = bucket.getKey();
                attrValueIdList.add(key);
            }
            skuLsResult.setAttrValueIdList(attrValueIdList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skuLsResult;
    }

    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if(skuLsParams.getKeyword()!=null){

           /* boolQuery.must(new MatchQueryBuilder("skuName",skuLsParams.getKeyword()) );

            //设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlight(highlightBuilder);*/
            MatchQueryBuilder ma = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            boolQuery.must(ma);
            // 设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 设置高亮字段
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            // 将高亮结果放入查询器中
            searchSourceBuilder.highlight(highlightBuilder);

        }
        //设置三级分类
        if(skuLsParams.getCatalog3Id()!=null){
            boolQuery.filter(new TermQueryBuilder("catalog3Id",skuLsParams.getCatalog3Id()));
        }
        //设置属性值
        if(skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            for (int i = 0; i <skuLsParams.getValueId().length ; i++) {
                String valueId = skuLsParams.getValueId()[i];
                boolQuery.filter(new TermQueryBuilder("skuAttrValueList.valueId",valueId));
            }
        }

        searchSourceBuilder.query(boolQuery);
        //设置分页
        searchSourceBuilder.from( (skuLsParams.getPageNo()-1) * skuLsParams.getPageSize() );
        searchSourceBuilder.size(skuLsParams.getPageSize());
        //设置排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        //设置聚合
        TermsBuilder groupby = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId").size(1000);
        searchSourceBuilder.aggregation(groupby);

        String toString = searchSourceBuilder.toString();
        System.out.println("toString:"+toString);
        return toString;
    }
}
