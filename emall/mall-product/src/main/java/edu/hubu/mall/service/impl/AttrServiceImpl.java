package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.dao.AttrDao;
import edu.hubu.mall.entity.AttrEntity;
import edu.hubu.mall.service.AttrService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 属性服务
 **/
@Service
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    /**
     * 根据id批量查询可用于搜索的属性
     * @param attrIds
     * @return
     */
    @Override
    public List<AttrEntity> queryAttrsByIds(List<Long> attrIds) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AttrEntity::getAttrId,attrIds);
        queryWrapper.eq(AttrEntity::getEnable,1);
        queryWrapper.eq(AttrEntity::getSearchType,1);
        return list(queryWrapper);
    }
}
