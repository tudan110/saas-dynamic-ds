package com.tudan.saas.dynamic.datasource.provider;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.tudan.saas.dynamic.datasource.config.MasterDataSourceProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 租户数据源提供者
 *
 * @author wangtan
 * @since 2025-08-22 13:20:39
 */
public class TenantDataSourceProvider extends AbstractJdbcDataSourceProvider {

    private final MasterDataSourceProperties properties;

    /**
     * 构造器注入
     *
     * @param defaultDataSourceCreator
     * @param properties
     */
    public TenantDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
                                    MasterDataSourceProperties properties) {
        super(defaultDataSourceCreator, properties.getDriverClassName(), properties.getUrl(), properties.getUsername(),
                properties.getPassword());
        this.properties = properties;
    }

    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM tenant_datasource WHERE status = 1");
        Map<String, DataSourceProperty> map = new HashMap<>(10);
        // 设置默认主数据源
        DataSourceProperty property = new DataSourceProperty();
        property.setUsername(properties.getUsername());
        property.setPassword(properties.getPassword());
        property.setUrl(properties.getUrl());
        map.put("master", property);
        // 从数据库读取
        while (rs.next()) {
            String name = rs.getString("tenant_id");
            String username = rs.getString("db_username");
            String password = rs.getString("db_password");
            String url = rs.getString("db_url");
            property = new DataSourceProperty();
            property.setUsername(username);
            property.setPassword(password);
            property.setUrl(url);
            map.put(name, property);
        }
        return map;
    }

}
