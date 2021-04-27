package edu.hubu.mall.search.service;

import edu.hubu.mall.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public interface SearchSaveService {
    boolean saveSpuInfoList(List<SkuEsModel> esModelList) throws IOException;
}
