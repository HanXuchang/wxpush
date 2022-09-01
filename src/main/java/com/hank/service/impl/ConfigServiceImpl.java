package com.hank.service.impl;

import com.hank.entity.Config;
import com.hank.mapper.ConfigMapper;
import com.hank.service.IConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 韩旭昌
 * @since 2022-09-01
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

}
