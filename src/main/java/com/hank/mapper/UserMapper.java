package com.hank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hank.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
