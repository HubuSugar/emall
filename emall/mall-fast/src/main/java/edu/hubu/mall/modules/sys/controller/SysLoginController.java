package edu.hubu.mall.modules.sys.controller;

import cn.hutool.core.util.IdUtil;
import com.wf.captcha.SpecCaptcha;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.utils.RedisUtil;
import edu.hubu.mall.modules.sys.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: huxiaoge
 * @Date: 2021/4/22
 * @Description: 后台登录相关接口
 **/
@RestController
@Slf4j
public class SysLoginController {


    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 动态生成验证码
     * @return
     */
    @GetMapping("/getDynamicCaptcha")
    public Result<String> generateCaptcha(){
        Result<String> result = Result.ok();
        try{
            SpecCaptcha captchaImg = captchaService.generateCaptcha(100, 38, 5);
            //获取验证码的数字，缓存到redis
            String verCode = captchaImg.text().toLowerCase();
            //随机生成UUID作为键
            String verKey = IdUtil.simpleUUID();
            log.info("verKey" + verKey + "verCode" + verCode);
            //将验证码存到redis,五分钟过期
            redisUtil.set(verKey,verCode,60 * 5);
            String base64Str = captchaImg.toBase64();
            result.setData(base64Str);
            Map<String,Object> extsMap = new HashMap<>();
            extsMap.put("verKey",verKey);
            result.setExtendMap(extsMap);
        }catch (Exception e){
            result.setMsg("生成验证码异常");
            result.setSuccess(false);
            result.setCode(1);
            e.printStackTrace();
        }
        return result;
    }


}
