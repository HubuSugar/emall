package edu.hubu.mall.order.web;

import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.order.Interceptor.LoginRequireInterceptor;
import edu.hubu.mall.order.feign.MemberFeignService;
import edu.hubu.mall.order.service.OrderService;
import edu.hubu.mall.order.to.OrderSubmitTo;
import edu.hubu.mall.order.vo.OrderConfirmVo;
import edu.hubu.mall.order.vo.OrderSubmitResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    /**
     * 提交订单
     * 下单之后需要执行的操作
     * 1、创建订单、验令牌、验价格、锁定库存、
     * 2、下单成功：支付页；失败：订单确认页
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitTo to){
        System.out.println(to);
        OrderSubmitResultVo submitResult = orderService.submitOrder(to);
        if(submitResult.getCode() == 0){
            //下单成功
            return "pay";
        }else{
            return "redirect:http://order.emall.com/toTrade";
        }
    }


}
