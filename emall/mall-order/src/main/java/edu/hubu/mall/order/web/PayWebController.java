package edu.hubu.mall.order.web;

import com.alipay.api.AlipayApiException;
import edu.hubu.mall.order.config.AlipayTemplate;
import edu.hubu.mall.order.service.OrderService;
import edu.hubu.mall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-01
 * @Description:
 **/
@Controller
@RequestMapping("/pay")
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 前端页面点击支付宝支付时调用
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/aliPayOrder",produces = "text/html")
    @ResponseBody
    public String pay(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getPayOrder(orderSn);
        //支付成功后，支付宝返回的一个支付成功页面
        return alipayTemplate.pay(payVo);
    }



}
