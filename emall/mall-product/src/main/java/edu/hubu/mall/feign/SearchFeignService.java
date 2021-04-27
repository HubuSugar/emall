package edu.hubu.mall.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.to.es.SkuEsModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@FeignClient("mall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/spuInfo")
    Result productSpuInfoUp(@RequestBody List<SkuEsModel> esModels);
}
