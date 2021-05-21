package edu.hubu.mall.fast.modules.sys.service;

import com.wf.captcha.SpecCaptcha;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/22
 * @Description:
 **/
public interface CaptchaService {

    /**
     * 生成图片验证码
     * @return 验证码对象
     */
    SpecCaptcha generateCaptcha(Integer width,Integer length,Integer num);

}
