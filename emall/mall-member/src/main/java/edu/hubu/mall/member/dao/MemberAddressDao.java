package edu.hubu.mall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.member.entity.MemberReceiveAddressEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Mapper
public interface MemberAddressDao extends BaseMapper<MemberReceiveAddressEntity> {
}
