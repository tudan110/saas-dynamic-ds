package com.tudan.saas.dynamic.datasource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多租户数据源注解：用于标记需要使用多租户数据源的方法或类
 * 支持 SpEL 表达式动态获取租户 ID
 *
 * @author wangtan
 * @since 2025-08-23 11:19:48
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantDS {

    /**
     * 数据源名称，支持 SpEL 表达式，例如：
     * - "master"
     * - "slave"
     * - "#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}"
     * - "#tenantId"
     */
    String value() default "#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}";

}
