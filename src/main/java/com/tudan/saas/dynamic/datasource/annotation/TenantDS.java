package com.tudan.saas.dynamic.datasource.annotation;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义租户数据源注解，封装默认的SpEL表达式
 * 等价于 @DS("#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}")
 *
 * @author wangtan
 * @since 2025-08-23 11:06:28
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 继承@DS注解，并设置默认值为租户SpEL表达式
@DS("#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}")
public @interface TenantDS {

    /**
     * 数据源名称，支持SpEL表达式
     *
     * @return 数据源名称
     */
    String value() default "#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}";

}
