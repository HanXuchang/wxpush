package com.hank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hank.entity.SysConfig;
import com.hank.mapper.SysConfigMapper;
import com.hank.service.ISysConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 韩旭昌
 * @since 2022-09-02
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public String getValue(String key) {
        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("param_key",key);
        return sysConfigMapper.selectOne(wrapper).getParamValue();
    }
}
