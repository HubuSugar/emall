package edu.hubu.mall.order.web;

import edu.hubu.mall.order.config.AlipayTemplate;
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

    @GetMapping("/aliPayOrder")
    @ResponseBody
    public String pay(@RequestParam("orderSn") String orderSn){

        return "hello";
    }



}
