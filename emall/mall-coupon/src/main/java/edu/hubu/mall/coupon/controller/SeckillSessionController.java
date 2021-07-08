package edu.hubu.mall.coupon.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;
import edu.hubu.mall.coupon.service.SeckillSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
@RestController
@RequestMapping("/coupon/seckillsession")
public class SeckillSessionController {

    @Autowired
    SeckillSessionService seckillSessionService;

    @GetMapping("/list")
    //@RequiresPermissions("coupon:seckillsession:list")
    public Result<PageUtil<SeckillSessionEntity>> queryPage(@RequestParam QueryParam queryParam){
        Result<PageUtil<SeckillSessionEntity>> ok = Result.ok();
        PageUtil<SeckillSessionEntity> pageData = seckillSessionService.queryPage(queryParam);
        ok.setData(pageData);
        return ok;
    }

    @PostMapping("/seckillSession/saveOrUpdate")
    public Result<String> saveOrUpdateSeckillSession(@RequestBody SeckillSessionEntity seckillSession){
        Result<String> ok = Result.ok();
        boolean  res = seckillSessionService.saveOrUpdateSeckillSession(seckillSession);
        if(res){
            ok.setData("操作成功");
        }else{
            ok = Result.error(1,"操作失败");
        }
        return  ok;
    }




}
