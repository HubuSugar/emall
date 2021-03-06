package edu.hubu.mall.product.webController;

import edu.hubu.mall.product.service.SkuInfoService;
import edu.hubu.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Description: 商品详情
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Controller
public class ItemController {


    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据skuId查询sku详情页数据
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = skuInfoService.querySkuDetailByIdAsync(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }

}
