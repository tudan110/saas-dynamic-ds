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
 *
 * @author wangtan
 * @since 2025-08-22 11:18:28
 */
@Service
// 类级别注解：通过 SpEL 调用 TenantContext 的静态方法获取 tenantId
@DS("#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // @DS("#tenantId")
    public List<SysUser> getuserList(String tenantId) {
        // 手动切换
        // DynamicDataSourceContextHolder.push(tenantId);
        List<SysUser> sysUsers = userMapper.selectList(null);
        DynamicDataSourceContextHolder.clear();
        return sysUsers;
    }

    // 保留 SpEL 表达式的 @DS 注解
    /* @DS("T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()")
    public List<SysUser> getuserList(String tenantId) {
        // 1. 手动解析 SpEL 表达式，查看结果（模拟 MyBatis-Plus 的解析逻辑）
        org.springframework.expression.ExpressionParser parser = new org.springframework.expression.spel.standard.SpelExpressionParser();
        String spelExpr = "T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()";
        String parsedTenantId = parser.parseExpression(spelExpr).getValue(String.class);
        System.out.println("SpEL 手动解析结果：" + parsedTenantId); // 关键日志

        return null;
    } */

}
