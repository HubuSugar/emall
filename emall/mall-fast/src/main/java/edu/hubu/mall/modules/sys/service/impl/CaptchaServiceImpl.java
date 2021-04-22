package edu.hubu.mall.modules.sys.service.impl;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import edu.hubu.mall.modules.sys.service.CaptchaService;
import org.springframework.stereotype.Service;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/22
 * @Description:
 **/
@Service("CaptchaServiceImpl")
public class CaptchaServiceImpl implements CaptchaService {

    /**
     * 动态生成验证码
     * @return
     */
    @Override
    public SpecCaptcha generateCaptcha(Integer width,Integer length,Integer num) {
        SpecCaptcha captchaImg = new SpecCaptcha(width,length,num);
        //生成的验证码只包含数字
        captchaImg.setCharType(Captcha.TYPE_ONLY_NUMBER);
        return captchaImg;
    }
}
