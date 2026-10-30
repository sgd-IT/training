---
title: "dynamic-datasource 必须配置 spring.datasource.dynamic.primary"
usage_scenario:
    - "Spring Boot 项目集成 dynamic-datasource 启动失败且提示 primary 缺失"
    - "多环境配置中 prod/test 正常但 dev 启动报 dynamic-datasource primary 错误"
    - "检查 dynamic-datasource 配置时发现 datasource 下无 dynamic 层级"
keywords:
    - "dynamic-datasource"
    - "primary配置"
    - "Spring Boot启动失败"
    - "application.yml"
---

使用 dynamic-datasource-spring-boot-starter（v3.3.1）时，必须显式配置 `spring.datasource.dynamic.primary` 指定主数据源名称，否则启动报错 `RuntimeException: dynamic-datasource Please check the setting of primary`。即使其他 datasource 配置完整，缺少此行即失败。（来源：Spring Boot 应用启动日志与 application-dev.yml 配置比对）
