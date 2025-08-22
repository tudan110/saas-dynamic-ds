package com.tudan.saas.dynamic.datasource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SaaS 动态数据源应用程序入口类
 *
 * @author wangtan
 * @since 2025-08-22 11:03:57
 */
@SpringBootApplication
@MapperScan("com.tudan.saas.dynamic.datasource.mapper")
public class TudanSaasDynamicDsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TudanSaasDynamicDsApplication.class, args);
    }

}
