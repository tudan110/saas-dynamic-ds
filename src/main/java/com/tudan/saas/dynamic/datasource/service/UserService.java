package com.tudan.saas.dynamic.datasource.service;

import com.tudan.saas.dynamic.datasource.domain.SysUser;

import java.util.List;

/**
 * 用户服务接口
 * 提供获取用户列表的功能
 *
 * @author wangtan
 * @since 2025-08-22 11:17:27
 */
public interface UserService {

    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    List<SysUser> getuserList();

}
