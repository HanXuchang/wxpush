package com.hank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hank.entity.UserEntity;
import com.hank.mapper.UserMapper;
import com.hank.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
}
