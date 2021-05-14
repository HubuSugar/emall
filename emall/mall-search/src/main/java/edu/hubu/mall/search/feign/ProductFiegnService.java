package edu.hubu.mall.search.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.search.vo.SearchResultVo;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-14
 **/
@FeignClient("mall-product")
public interface ProductFiegnService {



}
