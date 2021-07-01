package edu.hubu.mall.member.web;

import com.alibaba.fastjson.JSON;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 会员服务前端访问
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 支付成功后返回我的订单管理界面
     * @return
     */
    @GetMapping("/myOrder.html")
    public String orderList(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNum,Model model){

        //查出当前登录用户的所有订单列表数据
        Map<String,Object> page = new HashMap<>();
        page.put("pageNo",String.valueOf(pageNum));

        //查询当前登录人的订单数据
        Result<PageUtil<OrderVo>> pageResult = orderFeignService.queryMemberOrderList(page);
        System.out.println("pageResult:" + JSON.toJSONString(pageResult));
        model.addAttribute("orders",pageResult);

        return "myOrder";
    }
}
