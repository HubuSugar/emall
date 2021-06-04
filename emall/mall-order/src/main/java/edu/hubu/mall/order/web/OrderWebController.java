package edu.hubu.mall.order.web;

import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.order.Interceptor.LoginRequireInterceptor;
import edu.hubu.mall.order.feign.MemberFeignService;
import edu.hubu.mall.order.service.OrderService;
import edu.hubu.mall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/detail")
    public String orderList(){


        return "detail";
    }

    /**
     * 确定确认页
     * @param model
     * @return
     */
    @GetMapping("/toTrade")
    public String tradeConfirm(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData",confirmVo);
        return "confirm";
    }

}
