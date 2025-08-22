-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (  -- 用双引号包裹user，避免与关键字冲突
    id int4 NOT NULL PRIMARY KEY,
    name varchar(30) DEFAULT NULL,
    age int4 DEFAULT NULL,
    email varchar(50) DEFAULT NULL
);

-- 表注释
COMMENT ON TABLE "sys_user" IS '用户信息表';

-- 字段注释
COMMENT ON COLUMN "sys_user".id IS '主键ID';
COMMENT ON COLUMN "sys_user".name IS '姓名';
COMMENT ON COLUMN "sys_user".age IS '年龄';
COMMENT ON COLUMN "sys_user".email IS '邮箱';
