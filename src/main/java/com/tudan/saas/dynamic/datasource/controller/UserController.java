package com.tudan.saas.dynamic.datasource.controller;

import com.tudan.saas.dynamic.datasource.domain.SysUser;
import com.tudan.saas.dynamic.datasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器：用于处理用户相关的请求
 *
 * @author wangtan
 * @since 2025-08-22 15:51:27
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     *
     * @return String
     */
    @GetMapping("/list")
    public List<SysUser> list() {
        return userService.getuserList();
    }

}
