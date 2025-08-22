package com.tudan.saas.dynamic.datasource.holder;

/**
 * 租户上下文持有者
 *
 * @author wangtan
 * @since 2025-08-22 15:24:15
 */
public class TenantContext {

    private static final ThreadLocal<String> TENANT_HOLDER = new ThreadLocal<>();

    /**
     * 设置 tenantId 到当前线程
     */
    public static void setTenantId(String tenantId) {
        TENANT_HOLDER.set(tenantId);
    }

    /**
     * 获取当前线程的 tenantId
     *
     * @return 当前线程的 tenantId
     */
    public static String getTenantId() {
        return TENANT_HOLDER.get();
    }

    /**
     * 清除当前线程的 tenantId（避免内存泄漏）
     */
    public static void clear() {
        TENANT_HOLDER.remove();
    }

}
