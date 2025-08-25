package com.tudan.saas.dynamic.datasource.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类
 *
 * @author wangtan
 * @since 2025-08-22 11:13:42
 */
@Data
@TableName("sys_user")
public class SysUser {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
