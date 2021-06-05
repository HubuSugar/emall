package edu.hubu.mall.order.feign;

import edu.hubu.mall.common.ware.FareVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@FeignClient("mall-ware")
public interface WareFeignService {

    /**
     * 判断每一个skuId是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping(value="/ware/wareSku/hasStock")
    Map<Long, Boolean> getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * 查询邮寄费用
     * @param addrId
     * @return
     */
    @GetMapping("/ware/wareinfo/fare")
    FareVo getFare(@RequestParam("addrId") Long addrId);


}
