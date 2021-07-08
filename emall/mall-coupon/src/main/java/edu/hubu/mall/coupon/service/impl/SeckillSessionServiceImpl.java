package edu.hubu.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.constant.Constant;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.Query;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.dao.SeckillSessionDao;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;
import edu.hubu.mall.coupon.service.SeckillSessionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
@Service
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    /**
     * 分页查询秒杀场次的信息
     * @param queryParam
     * @return
     */
    @Override
    public PageUtil<SeckillSessionEntity> queryPage(QueryParam queryParam) {

        LambdaQueryWrapper<SeckillSessionEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(queryParam.getKey() != null){
            queryWrapper.eq(SeckillSessionEntity::getId,queryParam.getKey());
        }
        if(!StringUtils.isEmpty(queryParam.getName())){
            queryWrapper.eq(SeckillSessionEntity::getName,queryParam.getName());
        }
        HashMap<String, Object> pageMap = new HashMap<>();
        pageMap.put(Constant.PAGE_NO,queryParam.getPageNo());
        pageMap.put(Constant.PAGE_SIZE,queryParam.getPageSize());

        IPage<SeckillSessionEntity> page = this.page(new Query<SeckillSessionEntity>().getPage(pageMap),queryWrapper);
        return new PageUtil<>(page);
    }

    /**
     * 保存或者修改场次信息
     * @param seckillSession
     * @return
     */
    @Override
    public boolean saveOrUpdateSeckillSession(SeckillSessionEntity seckillSession) {
        //新增操作
        if(seckillSession.getId() == null){
            this.save(seckillSession);
        }else{
            SeckillSessionEntity old = this.getById(seckillSession.getId());
            if(old == null){
                return false;
            }
            seckillSession.setId(old.getId());
            this.updateById(seckillSession);
        }
        return true;
    }
}
