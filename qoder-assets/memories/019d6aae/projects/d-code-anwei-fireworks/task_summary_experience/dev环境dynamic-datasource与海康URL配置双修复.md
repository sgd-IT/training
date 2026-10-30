---
title: "dev环境dynamic-datasource与海康URL配置双修复"
usage_scenario:
    - "Spring Boot项目使用dynamic-datasource时dev环境启动报primary缺失"
    - "集成海康云平台SDK时HikVisionUtil初始化报URI not absolute"
    - "排查多profile配置不一致导致的启动失败"
keywords:
    - "dynamic-datasource"
    - "primary配置"
    - "海康云平台"
    - "application-dev.yml"
source: "auto"
---

## 任务描述
修复 Spring Boot 应用在 dev 环境启动失败问题，包含两个关联故障：1）baomidou dynamic-datasource 缺少 primary 配置；2）HikVisionUtil 初始化时因海康 URL 配置缺失导致 URI not absolute 异常。

## 执行过程
```mermaid
graph TD
    A[启动失败：dynamic-datasource primary缺失] --> B[对比application-dev.yml与prod/test配置]
    B --> C[发现dev仍用旧版spring.datasource而非dynamic结构]
    C --> D[将dev配置重构为dynamic层级：primary/master/strict]
    D --> E[启动仍失败：HikVisionUtil.URI not absolute]
    E --> F[定位HikVisionProperties绑定字段]
    F --> G[发现application-dev.yml缺失hikang.url.token/group/peopleUrl/closeUrl]
    G --> H[补充全部必需URL配置项]
```

## 任务总结
成功修复双阶段启动异常：1）统一 dev 环境 dynamic-datasource 配置结构，显式声明 primary: master；2）补全海康云平台所有必需 API 地址，确保 HikVisionUtil 初始化时 URL 字符串非空且绝对。
