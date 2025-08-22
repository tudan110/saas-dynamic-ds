package com.tudan.saas.dynamic.datasource.config;

import com.baomidou.dynamic.datasource.processor.DsJakartaHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsJakartaSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.tudan.saas.dynamic.datasource.processor.CustomDsSpelExpressionProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.expression.BeanFactoryResolver;

/**
 * 动态数据源配置类：用于配置动态数据源的处理器链
 * 主要包括请求头处理器、Session处理器和自定义SpEL处理器
 * 这些处理器用于解析请求中的租户信息，以实现多租户数据隔离
 *
 * @author wangtan
 * @since 2025-08-22 21:28:36
 */
@Configuration
public class DynamicDataSourceConfig {

    /**
     * 动态数据源处理器配置：
     * 1. 请求头处理器（解析#header.xxx）
     * 2. Session处理器（解析#session.xxx）
     * 3. 自定义SpEL处理器（修复ParserContext问题）用CustomDsSpelExpressionProcessor替换默认的DsSpelExpressionProcessor
     *
     * @param beanFactory Spring Bean工厂，用于解析SpEL表达式中的Bean
     * @return DsProcessor 处理器链
     */
    @Role(2)
    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor(BeanFactory beanFactory) {
        // 1. 请求头处理器（解析#header.xxx）
        DsProcessor headerProcessor = new DsJakartaHeaderProcessor();

        // 2. Session处理器（解析#session.xxx）
        DsProcessor sessionProcessor = new DsJakartaSessionProcessor();

        // 3. 自定义SpEL处理器（修复ParserContext问题）
        CustomDsSpelExpressionProcessor spelProcessor = new CustomDsSpelExpressionProcessor();
        // 必须设置BeanResolver，否则无法解析Spring容器中的Bean（如果表达式需要）
        spelProcessor.setBeanResolver(new BeanFactoryResolver(beanFactory));

        // 构建处理器链：header → session → 自定义SpEL
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelProcessor);

        return headerProcessor;
    }

}
