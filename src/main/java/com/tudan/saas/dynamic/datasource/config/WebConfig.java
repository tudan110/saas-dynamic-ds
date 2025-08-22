package com.tudan.saas.dynamic.datasource.config;

import com.tudan.saas.dynamic.datasource.interceptor.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类，用于注册拦截器
 *
 * @author wangtan
 * @since 2025-08-22 15:33:18
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求（可根据需要调整路径）
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/tenant/**")  // 排除以 /tenant 开头的路径
                .order(Ordered.HIGHEST_PRECEDENCE);
    }

}