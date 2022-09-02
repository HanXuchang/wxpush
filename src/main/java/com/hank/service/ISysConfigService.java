package com.hank.service;

import com.hank.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 韩旭昌
 * @since 2022-09-02
 */
public interface ISysConfigService extends IService<SysConfig> {

    public String getValue(String key);

}
