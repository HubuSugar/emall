package edu.hubu.mall.seckill.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.seckill.SeckillSkuVo;
import edu.hubu.mall.seckill.service.SeckillService;
import edu.hubu.mall.seckill.to.SeckillSkuTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-12
 * @Description: 秒杀服务
 **/
@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    /**
     * 根据当前时间查询秒杀场次信息
     * @return
     */
    @GetMapping("/getCurrentSeckillSkus")
    @ResponseBody
    public Result<List<SeckillSkuTo>> getCurrentSeckillSkus(){
        Result<List<SeckillSkuTo>> ok = Result.ok();
        List<SeckillSkuTo> skus = seckillService.getCurrentSeckillSkus();
        ok.setData(skus);
        return ok;
    }

    @GetMapping("/seckill/sku/{skuId}")
    @ResponseBody
    public Result<SeckillSkuVo> querySeckillSkuInfo(@PathVariable("skuId") Long skuId){
        Result<SeckillSkuVo> ok = Result.ok();
        SeckillSkuVo seckillSku = seckillService.querySeckillSkuInfo(skuId);
        ok.setData(seckillSku);
        return ok;
    }

    /**
     * 执行秒杀逻辑
     * @param killId : 秒杀场次_秒杀商品
     * @param key
     * @param num
     * @return
     */
    @GetMapping("/kill")
    public String kill(@RequestParam("killId") String killId, @RequestParam("") String key,
                       @RequestParam("num") Integer num, Model model){
        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("orderSn",orderSn);
        return "";
    }
}
