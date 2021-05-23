package edu.hubu.mall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.member.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/23
 * @Description:
 **/
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
}
