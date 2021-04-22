package edu.hubu.mall.modules.sys.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.wf.captcha.SpecCaptcha;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.utils.RedisUtil;
import edu.hubu.mall.modules.sys.entity.SysUserEntity;
import edu.hubu.mall.modules.sys.service.CaptchaService;
import edu.hubu.mall.modules.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 动态生成验证码
     * @return
     */
    @GetMapping("/sys/getDynamicCaptcha")
    public Result<String> generateCaptcha(){
        Result<String> result = Result.ok();
        try{
            SpecCaptcha captchaImg = captchaService.generateCaptcha(100, 38, 5);
            //获取验证码的数字，缓存到redis
            String verCode = captchaImg.text().toLowerCase();
            //随机生成UUID作为键
            String verKey = IdUtil.simpleUUID();
            log.info("verKey" + verKey + "verCode" + verCode);
            //将验证码存到redis,5分钟过期
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

    /**
     * 后台管理系统登录接口
     * @param username 用户名
     * @param password 密码
     * @param keycode 验证码
     * @param verCodeKey 验证码对应的键
     * @return
     * @throws Exception
     */
    @PostMapping("/sys/login")
    public Result<String> login(@RequestParam(value = "username", required = false) String username,
                                             @RequestParam(value = "password", required = false) String password,
                                             @RequestParam(value = "keycode", required = false) String keycode,
                                             @RequestParam(value = "verCodeKey", required = false) String verCodeKey) throws Exception {
        Result<String> result = new Result<>(false,1,"登录失败请重试！");
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            result.setMsg("输入的账号密码有误！");
            return result;
        }
        if(StringUtils.isBlank(keycode) || StringUtils.isBlank(verCodeKey)){
            result.setMsg("输入的验证码有误！");
            return result;
        }
        //判断验证码是否正确
        Object o = redisUtil.get(verCodeKey);
        if(null == o || !o.toString().equalsIgnoreCase(keycode)){
            result.setMsg("验证码输入错误！");
            return result;
        }
        //比较完成删除验证码
        redisUtil.del(verCodeKey);
        //判断用户名和密码是否正确
        SysUserEntity sysUserEntity = userService.queryByUsername(username);

        if(sysUserEntity == null ||
                !sysUserEntity.getPassword().equals(SecureUtil.sha256().digestHex(password + sysUserEntity.getSalt()))) {
            result.setMsg("账号或者密码错误");
            return result;
        }



        return result;
    }

}
