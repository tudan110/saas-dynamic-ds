package com.tudan.saas.dynamic.datasource.processor;

import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 自定义 SpEL 表达式处理器，用于动态数据源的 SpEL 表达式解析
 * 主要用于在方法调用时解析 SpEL 表达式以确定数据源
 *
 * @author wangtan
 * @since 2025-08-22 21:01:14
 */
@Setter
public class CustomDsSpelExpressionProcessor extends DsSpelExpressionProcessor {

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private final ParserContext parserContext = new ParserContext() {
        public boolean isTemplate() {
            return false;
        }

        public String getExpressionPrefix() {
            return null;
        }

        public String getExpressionSuffix() {
            return null;
        }
    };
    private ParserContext templateContext = new ParserContext() {
        public boolean isTemplate() {
            return true;
        }

        public String getExpressionPrefix() {
            return "#{";
        }

        public String getExpressionSuffix() {
            return "}";
        }
    };
    private BeanResolver beanResolver;

    public CustomDsSpelExpressionProcessor() {
    }

    public boolean matches(String key) {
        return true;
    }

    public String doDetermineDatasource(MethodInvocation invocation, String key) {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        DsSpelExpressionProcessor.ExpressionRootObject rootObject = new DsSpelExpressionProcessor.ExpressionRootObject(method, arguments, invocation.getThis());
        StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, arguments, NAME_DISCOVERER);
        context.setBeanResolver(this.beanResolver);

        // 动态选择解析器：含 #{ 则用模板模式，否则用普通模式
        ParserContext contextToUse = key.contains("#{") ? templateContext : parserContext;

        Object value = PARSER.parseExpression(key, contextToUse).getValue(context);
        return value == null ? null : value.toString();
    }

}

