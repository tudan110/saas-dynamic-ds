package com.tudan.saas.dynamic.datasource.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tudan.saas.dynamic.datasource.annotation.TenantDS;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多租户数据源切换切面：用于在方法执行前后切换数据源
 * 支持通过 SpEL 表达式动态获取租户 ID 并切换数据源
 *
 * @author wangtan
 * @since 2025-08-23 12:11:17
 */
@Aspect
@Component
public class TenantDSSwitchAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知：在方法执行前后切换数据源
     * 支持 @TenantDS 注解标记的方法或类
     */
    @Around("@annotation(com.tudan.saas.dynamic.datasource.annotation.TenantDS) || " +
            "within(@com.tudan.saas.dynamic.datasource.annotation.TenantDS *)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        TenantDS tenantDS = AnnotationUtils.findAnnotation(method, TenantDS.class);
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
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 精准解析 SpEL：兼容 #{...} 和纯 SpEL
     */
    private String parseSpEL(String spEL, Method method, Object[] args, MethodSignature signature) {
        EvaluationContext context = new StandardEvaluationContext();

        String[] paramNames = signature.getParameterNames();
        if (paramNames != null && args != null) {
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        String trimmed = spEL.trim();

        // ✅ 区分模板和纯表达式
        if (trimmed.startsWith("#{") && trimmed.endsWith("}")) {
            // 模板表达式：#{...}
            ParserContext templateParserContext = new TemplateParserContext();
            return parser.parseExpression(spEL, templateParserContext).getValue(context, String.class);
        } else {
            // 纯 SpEL 表达式：T(...), #xxx, master 等
            return parser.parseExpression(spEL).getValue(context, String.class);
        }
    }

}
