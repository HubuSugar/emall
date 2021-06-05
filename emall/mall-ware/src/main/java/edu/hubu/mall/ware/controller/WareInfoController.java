package edu.hubu.mall.ware.controller;

import edu.hubu.mall.ware.service.WareInfoService;
import edu.hubu.mall.common.ware.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@RestController
@RequestMapping("/ware/wareinfo")
public class WareInfoController {

    @Autowired
    WareInfoService wareInfoService;

    /**
     * 查询邮寄费用
     * @param addrId
     * @return
     */
    @GetMapping("/fare")
    public FareVo getFare(@RequestParam("addrId") Long addrId){
        return  wareInfoService.getFare(addrId);
    }


}
