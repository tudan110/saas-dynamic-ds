package com.tudan.saas.dynamic.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SaaS 主数据源配置属性类
 *
 * @author wangtan
 * @since 2025-08-22 11:20:13
 */
@Data
@ConfigurationProperties("spring.datasource.dynamic.datasource.master")
public class MasterDataSourceProperties {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * jdbc url
     */
    private String url;

    /**
     * 驱动类型
     */
    private String driverClassName;

}
