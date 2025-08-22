-- 创建租户数据源表（仅包含字段定义，无尾随COMMENT）
CREATE TABLE tenant_datasource (
    tenant_id VARCHAR(32) PRIMARY KEY,  -- 主键字段
    db_url VARCHAR(200) NOT NULL,       -- 非空字段
    db_username VARCHAR(50) NOT NULL,   -- 非空字段
    db_password VARCHAR(100) NOT NULL,  -- 非空字段
    driver_class_name VARCHAR(100) DEFAULT 'org.postgresql.Driver',  -- 默认驱动类
    status SMALLINT DEFAULT 1,          -- 状态默认值1
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP  -- 自动填充当前时间
);

-- 给表添加注释
COMMENT ON TABLE tenant_datasource IS '租户数据源配置表，存储各租户的数据库连接信息';

-- 给每个字段添加注释
COMMENT ON COLUMN tenant_datasource.tenant_id IS '租户ID（主键）';
COMMENT ON COLUMN tenant_datasource.db_url IS '数据库URL（如 jdbc:postgresql://localhost:5432/tenant_db_1?useUnicode=true&characterEncoding=utf8&ssl=false）';
COMMENT ON COLUMN tenant_datasource.db_username IS '数据库用户名';
COMMENT ON COLUMN tenant_datasource.db_password IS '数据库密码';
COMMENT ON COLUMN tenant_datasource.driver_class_name IS '数据库驱动类（默认PostgreSQL驱动）';
COMMENT ON COLUMN tenant_datasource.status IS '状态（0:禁用 1:启用）';
COMMENT ON COLUMN tenant_datasource.create_time IS '记录创建时间（带时区）';