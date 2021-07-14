package edu.hubu.mall.seckill.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.seckill.SeckillSkuVo;
import edu.hubu.mall.seckill.service.SeckillService;
import edu.hubu.mall.seckill.to.SeckillSkuTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-12
 * @Description: 秒杀服务
 **/
@RestController
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    /**
     * 根据当前时间查询秒杀场次信息
     * @return
     */
    @GetMapping("/getCurrentSeckillSkus")
    public Result<List<SeckillSkuTo>> getCurrentSeckillSkus(){
        Result<List<SeckillSkuTo>> ok = Result.ok();
        List<SeckillSkuTo> skus = seckillService.getCurrentSeckillSkus();
        ok.setData(skus);
        return ok;
    }

    @GetMapping("/seckill/sku/{skuId}")
    public Result<SeckillSkuVo> querySeckillSkuInfo(@PathVariable("skuId") Long skuId){
        Result<SeckillSkuVo> ok = Result.ok();
        SeckillSkuVo seckillSku = seckillService.querySeckillSkuInfo(skuId);
        ok.setData(seckillSku);
        return ok;
    }
}
