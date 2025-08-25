package com.tudan.saas.dynamic.datasource.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 租户数据源配置实体类
 *
 * @author wangtan
 * @since 2025-08-22 11:13:35
 */
@Data
@TableName("tenant_datasource")
public class TenantDatasource {

    @TableId(value = "tenant_id", type = IdType.INPUT)
    private String tenantId;

    @TableField("db_url")
    private String dbUrl;

    @TableField("db_username")
    private String dbUsername;

    @TableField("db_password")
    private String dbPassword;

    @TableField("driver_class_name")
    private String driverClassName = "org.postgresql.Driver";

    @TableField("status")
    private Integer status = 1; // 状态默认启用

    @TableField("create_time")
    private Date createTime;

}
