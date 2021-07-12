package edu.hubu.mall.seckill.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.seckill.service.SeckillService;
import edu.hubu.mall.seckill.to.SeckillSkuTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/getCurrentSeckillSkus")
    public Result<List<SeckillSkuTo>> getCurrentSeckillSkus(){
        Result<List<SeckillSkuTo>> ok = Result.ok();
        List<SeckillSkuTo> skus = seckillService.getCurrentSeckillSkus();
        ok.setData(skus);
        return ok;
    }
}
