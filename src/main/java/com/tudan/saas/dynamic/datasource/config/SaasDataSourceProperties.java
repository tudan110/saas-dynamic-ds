package com.tudan.saas.dynamic.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SaaS 数据源配置属性类
 *
 * @author wangtan
 * @since 2025-08-22 2025-08-22 14:52:22
 */
@Data
@ConfigurationProperties("saas.datasource")
public class SaasDataSourceProperties {

    /**
     * 数据库前缀
     */
    private String dbPrefix;

    /**
     * jdbc url 前缀
     */
    private String urlPrefix;

    /**
     * jdbc url 后缀
     */
    private String urlSuffix;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 驱动类型
     */
    private String driverClassName;

}
