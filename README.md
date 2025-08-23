# saas-dynamic-datasource

基于 MyBatis-Plus Dynamic Datasource 的多租户动态数据源解决方案，适配 PostgreSQL
数据库，支持租户注册时自动创建独立数据库及表结构，实现租户数据隔离与灵活的数据源路由。

## 核心特性

✅ **多租户数据隔离**：每个租户对应独立 PostgreSQL 数据库，避免数据混杂，提升安全性  
✅ **动态数据源路由**：基于 MyBatis-Plus 动态数据源组件，支持注解式（`@DS`）方法级数据源切换  
✅ **自动建库建表**：租户注册时自动创建专属数据库及初始化表结构，简化运维流程  
✅ **PostgreSQL 深度适配**：完整兼容 PostgreSQL 语法（如 `SERIAL` 自增、`TIMESTAMPTZ` 时区类型等）  
✅ **轻量级集成**：无侵入式设计，可快速接入 Spring Boot 项目，配置简单

## 技术栈

- **核心框架**：Spring Boot 3.x + MyBatis-Plus 3.x
- **动态数据源**：MyBatis-Plus Dynamic Datasource
- **数据库**：PostgreSQL 14+
- **构建工具**：Maven

## 快速开始

### 1. 环境准备

- JDK 17+
- PostgreSQL 14+（需提前创建默认主库 `saas_master_db`）
- Maven 3.6+

### 2. 配置主数据源

在 `application.yml` 中配置主库连接（用于管理租户数据源信息）：

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:postgresql://localhost:5432/saas_master_db
          username: postgres
          password: 123456
          driver-class-name: org.postgresql.Driver
```

### 3. 租户注册与数据源初始化

调用租户注册接口，系统会自动：

1. 在主库 `tenant_datasource` 表中记录租户数据源配置
2. 为租户创建独立数据库（如 `tenant_db_xxx`）
3. 初始化租户库表结构（如 `user` 表）

### 4. 切换租户数据源

通过 `@TenantDS` 注解指定租户数据源：

- 默认表达式：#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}
- 类注解和方法注解都能识别
- SpEL 表达式兼容 #{} 和无 #{} 写法

```java
@Service
@TenantDS
public class UserServiceImpl implements UserService {
}
```

通过 `@DS` 注解指定租户数据源：

```java
@Service
// 类级别注解：通过 SpEL 调用 TenantContext 的静态方法获取 tenantId
@DS("#{T(com.tudan.saas.dynamic.datasource.holder.TenantContext).getTenantId()}")
public class UserServiceImpl implements UserService {
}
```

或者

```java
@Service
public class UserServiceImpl {
    // 操作租户数据库（数据源标识为租户ID）
    @DS("#tenantId")
    public User getUserById(String tenantId, Long id) {
        return userMapper.selectById(id);
    }
}
```

## 适用场景

- **SaaS 平台多租户系统**：需严格隔离各租户数据的场景
- **动态数据源切换需求**：如主从分离、多业务库路由等
- **PostgreSQL 生态项目**：需要适配 PostgreSQL 特性的动态数据源方案

## 目录结构

```
saas-dynamic-datasource/
├── src/
│   ├── main/
│   │   ├── java/com/tudan/saas/dynamic/datasource/
│   │   │   ├── config/        # 动态数据源配置
│   │   │   ├── controller/    # 租户注册接口
│   │   │   ├── domain/        # 实体类（租户数据源、用户等）
│   │   │   ├── holder/        # 租户上下文持有者
│   │   │   ├── interceptor/   # 接口拦截器（从 request 中获取租户 ID）
│   │   │   ├── mapper/        # MyBatis Mapper
│   │   │   ├── processor/     # 自定义 SpEL 表达式处理器，用于动态数据源的 SpEL 表达式解析
│   │   │   ├── provider/      # 数据源提供者（租户数据源注册与管理）
│   │   │   └── service/       # 业务逻辑（租户库创建、数据源管理）
│   │   └── resources/
│   │       └── application.yml # 全局配置
│   └── test/                  # 单元测试
└── pom.xml                    # Maven 依赖
```

## 许可证

[MIT](LICENSE)

## 贡献指南

欢迎提交 Issue 或 PR 参与项目优化！提交前请确保代码符合项目编码规范，并添加相关测试用例。

如有问题，可通过 Issue 反馈