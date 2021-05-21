package edu.hubu.mall.search.feign;

import edu.hubu.mall.search.entity.AttrEntity;
import edu.hubu.mall.search.entity.BrandEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-14
 **/
@FeignClient("mall-product")
public interface ProductFiegnService {

    @PostMapping("/product/attr/getAttrInfos")
    List<AttrEntity> queryAttrsByIds(@RequestBody  List<Long> ids);

    @GetMapping("/product/brand/getBrandInfos")
    List<BrandEntity> queryBrandByIds(@RequestParam("ids") List<Long> ids);

}
