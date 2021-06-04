package edu.hubu.mall.member.web;

import edu.hubu.mall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
     * 我的订单管理界面
     * @return
     */
    @GetMapping("/myOrder.html")
    public String orderList(Model model){




        return "myOrder";
    }
}
