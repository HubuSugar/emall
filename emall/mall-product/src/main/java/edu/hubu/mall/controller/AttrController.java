package edu.hubu.mall.controller;

import edu.hubu.mall.entity.AttrEntity;
import edu.hubu.mall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据属性ids查询属性集合
     * @param ids
     * @return
     */
    @PostMapping("/getAttrInfos")
    public List<AttrEntity> queryAttrInfosByIds(@RequestBody List<Long> ids){
        return attrService.queryAttrsByIds(ids);
    }



}
