package edu.hubu.mall.fast.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.fast.modules.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
@Mapper
public interface UserDao extends BaseMapper<SysUserEntity> {

    /**
     * 查询用户的所有权限
     * @param userId  用户ID
     */
    List<String> queryAllPerms(Long userId);
    /**
     * 查询用户菜单id
     * @param userId 用户id
     * @return
     */
    List<Long> queryAllMenuId(Long userId);


}
