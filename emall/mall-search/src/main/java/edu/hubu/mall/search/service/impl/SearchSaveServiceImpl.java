package edu.hubu.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import edu.hubu.mall.common.es.SkuEsModel;
import edu.hubu.mall.search.config.MallElasticConfig;
import edu.hubu.mall.search.constant.ElasticConstant;
import edu.hubu.mall.search.service.SearchSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Service
@Slf4j
public class SearchSaveServiceImpl implements SearchSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品上架服务
     * @param esModelList
     * @return
     */
    @Override
    public boolean saveSpuInfoList(List<SkuEsModel> esModelList) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel vo:esModelList) {
            IndexRequest indexRequest = new IndexRequest(ElasticConstant.PRODUCT_ES_INDEX,ElasticConstant.PRODUCT_ES_TYPE);
            indexRequest.id(String.valueOf(vo.getSkuId()));
            String esModelStr = JSON.toJSONString(vo);
            indexRequest.source(esModelStr,XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
//        System.out.println(bulkRequest);
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, MallElasticConfig.COMMON_OPTIONS);

        boolean b = bulkResponse.hasFailures();
        List<String> collect = Arrays.asList(bulkResponse.getItems()).stream().map(BulkItemResponse::getId).collect(Collectors.toList());
        log.info("商品上架完成 {}",collect);
        return b;
    }
}
