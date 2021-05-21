package edu.hubu.mall.fast.modules.sys.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.wf.captcha.SpecCaptcha;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.fast.common.utils.RedisUtil;
import edu.hubu.mall.fast.modules.sys.entity.SysUserEntity;
import edu.hubu.mall.fast.modules.sys.service.CaptchaService;
import edu.hubu.mall.fast.modules.sys.service.UserService;
import edu.hubu.mall.fast.modules.sys.service.UserTokenService;
import edu.hubu.mall.fast.modules.sys.vo.LoginFormVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private UserTokenService userTokenService;

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
     * @param loginFormVo 登录信息
     * @return
     * @throws Exception
     */
    @PostMapping("/sys/login")
    public Result<String> login(@RequestBody LoginFormVo loginFormVo) throws Exception {
        Result<String> result = new Result<>(false,1,"登录失败请重试！");
        if(loginFormVo == null || StringUtils.isBlank(loginFormVo.getUsername()) || StringUtils.isBlank(loginFormVo.getPassword())){
            result.setMsg("输入的账号密码有误！");
            return result;
        }
        if(StringUtils.isBlank(loginFormVo.getKeyCode()) || StringUtils.isBlank(loginFormVo.getVerCodeKey())){
            result.setMsg("输入的验证码有误！");
            return result;
        }
        //判断验证码是否正确
        Object o = redisUtil.get(loginFormVo.getVerCodeKey());
        if(null == o || !o.toString().equalsIgnoreCase(loginFormVo.getKeyCode())){
            result.setMsg("验证码输入错误！");
            return result;
        }
        //比较完成删除验证码
        redisUtil.del(loginFormVo.getVerCodeKey());
        //判断用户名和密码是否正确
        SysUserEntity sysUserEntity = userService.queryByUsername(loginFormVo.getUsername());
        String md5EncryptPwd = DigestUtil.md5Hex(loginFormVo.getPassword() + sysUserEntity.getSalt());
        log.info("加密后的密码为：" + md5EncryptPwd);
        if(null == sysUserEntity ||
                !sysUserEntity.getPassword().equals(md5EncryptPwd)) {
            result.setMsg("账号或者密码错误");
            return result;
        }

        //生成或者更新token
        String userToken = userTokenService.createUserToken(sysUserEntity.getUserId());
        if(StringUtils.isBlank(userToken)){
            return result;
        }

        result.setData(userToken);
        result.setSuccess(true);
        result.setMsg("成功");
        result.setCode(0);
        return result;
    }

}
