package edu.hubu.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.seckill.SeckillSessionVo;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtil<SeckillSessionEntity> queryPage(QueryParam queryParam);

    /**
     * 保存或者更新场次信息
     * @param seckillSession
     * @return
     */
    boolean saveOrUpdateSeckillSession(SeckillSessionEntity seckillSession);

    List<SeckillSessionVo> getLatest3DaysSessions();
}
