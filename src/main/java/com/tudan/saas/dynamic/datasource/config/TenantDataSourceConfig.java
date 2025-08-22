package com.tudan.saas.dynamic.datasource.config;

import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.tudan.saas.dynamic.datasource.provider.TenantDataSourceProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 租户数据源配置类
 *
 * @author wangtan
 * @since 2025-08-22 11:14:26
 */
@Configuration
@EnableConfigurationProperties({MasterDataSourceProperties.class, SaasDataSourceProperties.class})
public class TenantDataSourceConfig {

    @Bean
    public TenantDataSourceProvider tenantDataSourceProvider(DefaultDataSourceCreator dataSourceCreator,
                                                             MasterDataSourceProperties properties) {
        return new TenantDataSourceProvider(dataSourceCreator, properties);
    }

}
