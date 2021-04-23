package edu.hubu.mall.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.modules.sys.entity.SysUserTokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
@Mapper
public interface UserTokenDao extends BaseMapper<SysUserTokenEntity> {
}
