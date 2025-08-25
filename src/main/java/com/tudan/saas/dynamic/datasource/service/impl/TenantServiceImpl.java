package com.tudan.saas.dynamic.datasource.service.impl;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.hikaricp.HikariCpConfig;
import com.tudan.saas.dynamic.datasource.config.MasterDataSourceProperties;
import com.tudan.saas.dynamic.datasource.config.SaasDataSourceProperties;
import com.tudan.saas.dynamic.datasource.domain.po.TenantDatasource;
import com.tudan.saas.dynamic.datasource.mapper.TenantDatasourceMapper;
import com.tudan.saas.dynamic.datasource.service.TenantService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * 租户服务实现类
 *
 * @author wangtan
 * @since 2025-08-25 09:49:54
 */
@Service
public class TenantServiceImpl implements TenantService {

    private final MasterDataSourceProperties masterDbProperties;
    private final SaasDataSourceProperties saasDataSourceProperties;
    private final TenantDatasourceMapper tenantDatasourceMapper;
    private final DefaultDataSourceCreator dataSourceCreator;
    private final DataSource dataSource;

    /**
     * 构造器注入
     *
     * @param masterDbProperties       主数据源配置
     * @param saasDataSourceProperties SaaS 数据源配置
     * @param tenantDatasourceMapper   租户数据源映射器
     * @param dataSourceCreator        数据源创建器
     * @param dataSource               动态数据源
     */
    public TenantServiceImpl(MasterDataSourceProperties masterDbProperties, SaasDataSourceProperties saasDataSourceProperties, TenantDatasourceMapper tenantDatasourceMapper, DefaultDataSourceCreator dataSourceCreator, DataSource dataSource) {
        this.masterDbProperties = masterDbProperties;
        this.saasDataSourceProperties = saasDataSourceProperties;
        this.tenantDatasourceMapper = tenantDatasourceMapper;
        this.dataSourceCreator = dataSourceCreator;
        this.dataSource = dataSource;
    }

    /**
     * 获取当前所有数据源
     *
     * @return 数据源集合
     */
    @Override
    public Set<String> listAllDatasource() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        return ds.getDataSources().keySet();
    }

    /**
     * 注册租户
     *
     * @param tenantId 租户ID
     * @return 注册结果
     */
    @Override
    public String registerTenant(String tenantId) {

        // 1. 模拟一个用户注册后配置数据库连接
        TenantDatasource tenantDatasource = new TenantDatasource();
        tenantDatasource.setTenantId(tenantId);
        tenantDatasource.setDbUrl(this.saasDataSourceProperties.getUrlPrefix() + "/" + this.saasDataSourceProperties.getDbPrefix() + tenantId + this.saasDataSourceProperties.getUrlSuffix());
        tenantDatasource.setDbUsername(this.saasDataSourceProperties.getUsername());
        tenantDatasource.setDbPassword(this.saasDataSourceProperties.getPassword());

        // todo: 2. 模拟中间件对租户注册自动创建数据库
        createTenantDatabase(tenantDatasource);

        // 构建完成载插入租户数据库配置表
        tenantDatasourceMapper.insert(tenantDatasource);

        // 3. 动态注册数据源
        DataSourceProperty dataSourceProperty = convertToProp(tenantDatasource);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource newDataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(tenantDatasource.getTenantId(), newDataSource);
        return "租户注册成功";

    }

    /**
     * 移除租户数据源
     *
     * @param tenantId 租户ID
     * @return 移除结果
     */
    @Override
    public String remove(String tenantId) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(tenantId);
        return "移除成功";
    }

    /**
     * 模拟中间件对租户注册自动创建数据库
     *
     * @param tenantDatasource 租户数据源配置
     */
    private void createTenantDatabase(TenantDatasource tenantDatasource) {
        try (Connection conn = DriverManager.getConnection(this.masterDbProperties.getUrl(), this.masterDbProperties.getUsername(), this.masterDbProperties.getPassword())) {
            // PostgreSQL 创建数据库语法（无 IF NOT EXISTS，需手动判断）
            String dbName = this.saasDataSourceProperties.getDbPrefix() + tenantDatasource.getTenantId();

            // 1. 检查数据库是否已存在
            ResultSet rs = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            while (rs.next()) {
                String existingDbName = rs.getString(1);
                if (dbName.equals(existingDbName)) {
                    dbExists = true;
                    break;
                }
            }

            // 2. 不存在则创建数据库
            if (!dbExists) {
                String createDbSql = String.format("CREATE DATABASE \"%s\"", dbName);
                conn.createStatement().executeUpdate(createDbSql);
            }

            // 3. 连接到新创建的数据库，创建用户表
            String saasDbUrl = this.saasDataSourceProperties.getUrlPrefix() + "/" + dbName;
            try (Connection tenantConn = DriverManager.getConnection(saasDbUrl, this.saasDataSourceProperties.getUsername(), this.saasDataSourceProperties.getPassword())) {
                // PostgreSQL 建表语句（适配自增主键、移除反引号和引擎配置）
                String createTableSql = "CREATE TABLE IF NOT EXISTS sys_user (" + "  id int4 NOT NULL PRIMARY KEY," + "  name varchar(30) NOT NULL," + "  age int4 DEFAULT NULL," + "  email varchar(50) NOT NULL" + ")";
                tenantConn.createStatement().executeUpdate(createTableSql);

                // 添加字段注释（PostgreSQL 需单独执行）
                tenantConn.createStatement().executeUpdate("COMMENT ON TABLE sys_user IS '用户信息表'");
                tenantConn.createStatement().executeUpdate("COMMENT ON COLUMN sys_user.id IS '主键ID'");
                tenantConn.createStatement().executeUpdate("COMMENT ON COLUMN sys_user.name IS '姓名'");
                tenantConn.createStatement().executeUpdate("COMMENT ON COLUMN sys_user.age IS '年龄'");
                tenantConn.createStatement().executeUpdate("COMMENT ON COLUMN sys_user.email IS '邮箱'");
            }

        } catch (SQLException e) {
            throw new RuntimeException("创建租户数据库失败", e);
        }
    }

    /**
     * 将 TenantDatasource 转换为 DataSourceProperty
     *
     * @param tenantDatasource 租户数据源配置
     */
    private DataSourceProperty convertToProp(TenantDatasource tenantDatasource) {
        DataSourceProperty prop = new DataSourceProperty();
        prop.setUrl(tenantDatasource.getDbUrl());
        prop.setUsername(tenantDatasource.getDbUsername());
        prop.setPassword(tenantDatasource.getDbPassword());
        prop.setDriverClassName(tenantDatasource.getDriverClassName());
        prop.setPoolName(tenantDatasource.getTenantId() + "-pool"); // 连接池命名（便于监控）

        // 可扩展 Hikari 连接池参数（示例）
        prop.setHikari(new HikariCpConfig());
        prop.getHikari().setMaximumPoolSize(10);
        prop.getHikari().setConnectionTimeout(30000L);
        return prop;
    }

}
