package edu.hubu.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.ware.dao.WareOrderTaskDetailDao;
import edu.hubu.mall.ware.entity.WareOrderTaskDetailEntity;
import edu.hubu.mall.ware.service.WareOrderTaskDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-06-16
 * @Description:
 **/
@Service
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public List<WareOrderTaskDetailEntity> getTaskDetailByTaskId(Long taskId) {
        QueryWrapper<WareOrderTaskDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id",taskId);
        queryWrapper.eq("lock_status",1);
        return list(queryWrapper);
    }
}
