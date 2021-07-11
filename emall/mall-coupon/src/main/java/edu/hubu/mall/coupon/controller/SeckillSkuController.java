package edu.hubu.mall.coupon.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;
import edu.hubu.mall.coupon.entity.SeckillSkuEntity;
import edu.hubu.mall.coupon.service.SeckillSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-09
 **/
@RestController
@RequestMapping("/coupon/seckillskurelation")
public class SeckillSkuController {

    @Autowired
    SeckillSkuService seckillSkuService;

    /**
     * 查询场次关联的商品信息
     * @param queryParam
     * @return
     */
    @GetMapping("/list")
    public Result<PageUtil<SeckillSkuEntity>> querySeckillSkuList(QueryParam queryParam){
        Result<PageUtil<SeckillSkuEntity>> result = Result.ok();
        PageUtil<SeckillSkuEntity> seckillSkus = seckillSkuService.querySeckillSkuList(queryParam);
        result.setData(seckillSkus);
        return result;
    }

    /**
     * 场次关联商品新增或者修改
     * @param skuEntity
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public Result<String> saveOrUpdateSeckillSku(@RequestBody SeckillSkuEntity skuEntity){
        Result<String> ok = Result.ok();
        boolean  res = seckillSkuService.saveOrUpdateSeckillSession(skuEntity);
        if(res){
            ok.setData("操作成功");
        }else{
            ok = Result.error(1,"操作失败");
        }
        return  ok;
    }

}
