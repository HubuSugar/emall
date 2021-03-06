package edu.hubu.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.ware.dao.WareOrderTaskDao;
import edu.hubu.mall.ware.entity.WareOrderTaskEntity;
import edu.hubu.mall.ware.service.WareOrderTaskService;
import org.springframework.stereotype.Service;

/**
 * @Author: huxiaoge
 * @Date: 2021-06-16
 * @Description:
 **/
@Service
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {
    /**
     * 根据订单号查询库存工作单
     * @param orderSn
     * @return
     */
    @Override
    public WareOrderTaskEntity getTaskInfoByOrderSn(String orderSn) {
        QueryWrapper<WareOrderTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn",orderSn);
        return getOne(queryWrapper);
    }
}
