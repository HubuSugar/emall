package edu.hubu.mall.modules.sys.service;

import com.wf.captcha.SpecCaptcha;

import java.awt.image.BufferedImage;

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
