package com.tudan.saas.dynamic.datasource.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tudan.saas.dynamic.datasource.annotation.TenantDS;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户数据源切换切面
 * <p>
 * 功能：
 * <ul>
 *   <li>支持方法和类级别的 @TenantDS</li>
 *   <li>兼容 #{...} 和纯 SpEL 表达式</li>
 *   <li>线程安全：每次创建独立 EvaluationContext</li>
 *   <li>高性能：缓存已解析的 Expression 对象</li>
 * </ul>
 *
 * @author wangtan
 * @since 2025-08-23 12:23:21
 */
@Aspect
@Component
public class TenantDSSwitchAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 缓存已解析的 Expression 对象
     * key: (T:表示模板 / S:表示纯SpEL) + 表达式字符串
     * 注意：Expression 是线程安全的，可以缓存
     * EvaluationContext 是上下文，不能缓存
     */
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    /**
     * 切点：匹配
     * - 方法上有 @TenantDS
     * - 类上有 @TenantDS 的所有方法
     */
    @Around("@annotation(com.tudan.saas.dynamic.datasource.annotation.TenantDS) || " +
            "within(@com.tudan.saas.dynamic.datasource.annotation.TenantDS *)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        // 优先：方法上的 @TenantDS
        TenantDS tenantDS = AnnotationUtils.findAnnotation(method, TenantDS.class);
        // 其次：类上的 @TenantDS
        if (tenantDS == null) {
            tenantDS = AnnotationUtils.findAnnotation(clazz, TenantDS.class);
        }

        if (tenantDS != null) {
            String key = tenantDS.value();
            String dataSourceKey = parseSpEL(key, method, point.getArgs(), signature);
            DynamicDataSourceContextHolder.push(dataSourceKey);
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll(); // 清理数据源栈
        }
    }

    /**
     * 解析 SpEL 表达式，支持：
     * - "#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}"
     * - "T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()"
     * - "#{#tenantId}"
     * - "#tenantId"
     * - "master"
     * - "db_#{#tenantId}"
     *
     * @param spEL      SpEL 表达式
     * @param method    方法对象
     * @param args      方法参数
     * @param signature 方法签名（用于获取参数名）
     * @return 解析后的数据源 key
     */
    private String parseSpEL(String spEL, Method method, Object[] args, MethodSignature signature) {
        // ✅ 每次创建新的上下文，保证线程安全
        EvaluationContext context = new StandardEvaluationContext();

        // 设置方法参数变量：#paramName, #p0, #a0
        String[] paramNames = signature.getParameterNames();
        if (paramNames != null && args != null) {
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 判断是否为 #{...} 模板格式
        boolean isTemplate = isTemplateExpression(spEL);
        String cacheKey = (isTemplate ? "T:" : "S:") + spEL;

        // ✅ 缓存 Expression 对象，避免重复解析（性能优化关键）
        Expression expr = expressionCache.computeIfAbsent(cacheKey, k -> {
            ParserContext parserContext = isTemplate ? new TemplateParserContext() : null;
            return isTemplate
                    ? parser.parseExpression(spEL, parserContext)
                    : parser.parseExpression(spEL);
        });

        return expr.getValue(context, String.class);
    }

    /**
     * 判断是否为模板表达式 #{...}
     */
    private boolean isTemplateExpression(String spEL) {
        if (spEL == null) return false;
        String trimmed = spEL.trim();
        return trimmed.startsWith("#{") && trimmed.endsWith("}");
    }

}
