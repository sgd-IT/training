---
title: "修复 dynamic-datasource 在 dev 环境缺少 primary 配置"
usage_scenario:
    - "Spring Boot 多环境部署中 dynamic-datasource 启动失败排查"
    - "统一管理 dev/prod/test 环境的 dynamic-datasource 配置结构"
    - "同类框架（如 sharding-jdbc）多数据源主从标识缺失问题复用诊断思路"
keywords:
    - "dynamic-datasource"
    - "primary配置"
    - "多环境配置一致性"
---

## 任务描述
修复 Spring Boot 应用因 baomidou dynamic-datasource 在 dev 环境缺失 `primary` 配置导致的启动失败问题。

## 执行过程
```mermaid
graph TD
    A[日志报错: dynamic-datasource Please check the setting of primary] --> B[定位激活 profile 为 dev]
    B --> C[全局搜索 application-*.yml 配置文件]
    C --> D[对比 dev/prod/test 三套配置]
    D --> E[发现 application-dev.yml 仍用旧版 spring.datasource 单源格式]
    E --> F[重构 application-dev.yml 为 dynamic-datasource 格式]
    F --> G[嵌套 master 数据源并设置 primary: master 和 strict: false]
```

## 任务总结
成功解决 dynamic-datasource 启动异常：通过环境配置一致性比对，将 application-dev.yml 迁移至标准 dynamic-datasource 结构，确保 `spring.datasource.dynamic.primary` 正确指向 `master`，且 `strict: false` 提供容错能力。
