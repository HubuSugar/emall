package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.dao.SpuInfoDao;
import edu.hubu.mall.entity.SpuInfoEntity;
import edu.hubu.mall.service.SpuInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public List<SpuInfoEntity> querySpuInfoPageByCondition(Map<String, Object> paramMap) {
        return null;
    }
}
