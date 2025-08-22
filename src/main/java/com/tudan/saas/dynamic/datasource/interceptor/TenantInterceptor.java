package com.tudan.saas.dynamic.datasource.interceptor;

import com.tudan.saas.dynamic.datasource.holder.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户拦截器：用于在请求处理之前获取租户标识，并存储到 ThreadLocal 中
 * 这样可以在后续的服务调用中使用租户标识进行数据隔离
 *
 * @author wangtan
 * @since 2025-08-22 15:27:10
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前执行：从请求头中获取租户标识 tenantId，并存储到 ThreadLocal 中
     *
     * @param request  请求
     * @param response 响应
     * @param handler  处理器对象
     * @return true 继续处理请求，false 中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId == null || tenantId.isEmpty()) {
            // 处理无 tenantId 的情况（如抛异常或使用默认租户）
            throw new RuntimeException("缺少租户标识 X-Tenant-Id");
        }
        TenantContext.setTenantId(tenantId);
        return true;
    }

    // 请求处理完成后执行：清除 ThreadLocal 中的 tenantId（必须！）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }

}