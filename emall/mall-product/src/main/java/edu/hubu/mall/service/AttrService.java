package edu.hubu.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.to.es.SkuEsModel;
import edu.hubu.mall.entity.AttrEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public interface AttrService extends IService<AttrEntity> {

    List<AttrEntity> queryAttrsByIds(List<Long> attrIds);
}
