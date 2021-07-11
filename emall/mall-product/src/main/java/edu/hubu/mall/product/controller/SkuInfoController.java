package edu.hubu.mall.product.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.product.SkuInfoVo;
import edu.hubu.mall.product.entity.SkuInfoEntity;
import edu.hubu.mall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@RestController
@RequestMapping("/product/skuInfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据skuId查询sku_info
     */
    @GetMapping("/querySkuInfo/{skuId}")
    public SkuInfoVo querySkuInfoById(@PathVariable("skuId") Long skuId){
        return skuInfoService.querySkuInfoById(skuId);
    }

    /**
     * 根据skuIds批量查询skuInfo
     */
    @GetMapping("/querySkuInfos")
    public Result<List<SkuInfoVo>> querySkuInfoBatch(@RequestParam("ids") Set<Long> ids){
        Result<List<SkuInfoVo>> ok = Result.ok();
        List<SkuInfoVo> skuInfos = skuInfoService.querySkuInfos(ids);
        ok.setData(skuInfos);
        return ok;
    }

}
