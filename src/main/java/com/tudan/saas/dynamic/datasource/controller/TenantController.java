package com.tudan.saas.dynamic.datasource.controller;

import com.tudan.saas.dynamic.datasource.service.TenantService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author wangtan
 * @since 2025-08-22 14:18:40
 */

@RestController
@RequestMapping("/tenant")
public class TenantController {

    private final TenantService tenantService;

    /**
     * 构造器注入
     *
     * @param tenantService 租户服务
     */
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * 获取当前所有数据源
     */
    @GetMapping("/listAll")
    public Set<String> listAllDatasource() {
        return tenantService.listAllDatasource();
    }

    /**
     * 注册租户
     */
    @PostMapping("/register")
    public String registerTenant(String tenantId) {
        return tenantService.registerTenant(tenantId);
    }

    /**
     * 移除租户数据源
     */
    @DeleteMapping("/remove")
    public String remove(String tenantId) {
        return tenantService.remove(tenantId);
    }

}
