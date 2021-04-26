package edu.hubu.mall.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.entity.SpuInfoEntity;
import edu.hubu.mall.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
@RestController
@RequestMapping("/product/spuInfo")
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 根据条件分页查询spuInfo数据
     * @return
     */
    @GetMapping("/list")
    public Result<List<SpuInfoEntity>> spuInfoList(@RequestParam Map<String,Object> paramMap){
        List<SpuInfoEntity> spuInfoEntities = spuInfoService.querySpuInfoPageByCondition(paramMap);
        Result<List<SpuInfoEntity>> result = Result.ok();
        result.setData(spuInfoEntities);
        return result;
    }

}
