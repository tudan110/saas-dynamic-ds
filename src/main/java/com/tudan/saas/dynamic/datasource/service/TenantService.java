package com.tudan.saas.dynamic.datasource.service;

import java.util.Set;

/**
 * 租户服务接口
 *
 * @author wangtan
 * @since 2025-08-25 09:48:44
 */
public interface TenantService {

    /**
     * 获取当前所有数据源
     *
     * @return 数据源集合
     */
    Set<String> listAllDatasource();

    /**
     * 注册租户
     *
     * @param tenantId 租户ID
     * @return 注册结果
     */
    String registerTenant(String tenantId);

    /**
     * 移除租户数据源
     *
     * @param tenantId 租户ID
     * @return 移除结果
     */
    String remove(String tenantId);

}
