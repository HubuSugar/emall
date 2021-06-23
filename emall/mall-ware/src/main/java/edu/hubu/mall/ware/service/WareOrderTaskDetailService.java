package edu.hubu.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.ware.entity.WareOrderTaskDetailEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-06-16
 * @Description:
 **/
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {
    List<WareOrderTaskDetailEntity> getTaskDetailByTaskId(Long id);
}
