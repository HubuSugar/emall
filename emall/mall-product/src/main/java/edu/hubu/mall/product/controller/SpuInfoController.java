package edu.hubu.mall.product.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.product.entity.SpuInfoEntity;
import edu.hubu.mall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据spuId进行商品上架
     */
    @PostMapping("/{spuId}/up")
    public Result spuUp(@PathVariable(value = "spuId") String spuId){
        Boolean upResult = spuInfoService.spuUp(Long.valueOf(spuId));
        if(upResult){
            return Result.ok();
        }else{
            return Result.error(1,"商品上架失败");
        }
    }

}
