package edu.hubu.mall.ware.controller;

import edu.hubu.mall.ware.WareSkuStockVo;
import edu.hubu.mall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@RestController
@RequestMapping("/ware/wareSku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 根据skuIds列表查询是否有库存
     * @param skuIds skuId列表
     * @return skuId - 是否有库存
     */
    @PostMapping(value="/hasStock")
    public Map<Long,Boolean> getSkuHasStock(@RequestBody List<Long> skuIds){
        return wareSkuService.getSkuHasStockBySkuIds(skuIds);
    }
}
