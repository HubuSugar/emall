package edu.hubu.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtil<SeckillSessionEntity> queryPage(QueryParam queryParam);

}
