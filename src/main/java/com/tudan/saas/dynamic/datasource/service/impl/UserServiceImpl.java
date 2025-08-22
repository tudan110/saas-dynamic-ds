package com.tudan.saas.dynamic.datasource.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tudan.saas.dynamic.datasource.domain.SysUser;
import com.tudan.saas.dynamic.datasource.mapper.UserMapper;
import com.tudan.saas.dynamic.datasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类：提供获取用户列表的功能
 * 使用 @DS 注解指定数据源，动态切换到对应的租户数据源
 * 类级别注解：通过 SpEL 调用 TenantContext 的静态方法获取 tenantId
 *
 * @author wangtan
 * @since 2025-08-22 11:18:28
 */
@Service
@DS("#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    public List<SysUser> getuserList() {
        // 手动切换
        // DynamicDataSourceContextHolder.push(tenantId);
        List<SysUser> sysUsers = userMapper.selectList(null);
        DynamicDataSourceContextHolder.clear();
        return sysUsers;
    }

}
