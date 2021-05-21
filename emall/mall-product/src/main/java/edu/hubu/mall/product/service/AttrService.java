package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.product.entity.AttrEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public interface AttrService extends IService<AttrEntity> {

    List<AttrEntity> queryAttrsByIds(List<Long> attrIds);
}
