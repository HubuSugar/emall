package edu.hubu.mall.controller;

import edu.hubu.mall.entity.AttrEntity;
import edu.hubu.mall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 商品属性服务
 * @Author: huxiaoge
 * @Date: 2021-05-14
 **/
@RestController
@RequestMapping("/product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @GetMapping("/getAttrInfos")
    public List<AttrEntity> queryAttrInfosByIds(@RequestParam List<Long> ids){
        return attrService.queryAttrsByIds(ids);
    }



}
