package edu.hubu.mall.product.controller;

import edu.hubu.mall.product.entity.BrandEntity;
import edu.hubu.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/16
 * @Description:
 **/
@RestController
@RequestMapping("/product/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/getBrandInfos")
    public List<BrandEntity> queryBrandsByIds(@RequestParam("ids") List<Long> ids){
        return brandService.queryBrandsByIds(ids);
    }
}
