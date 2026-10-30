---
title: "Spring Boot配置节点名需与占位符完全匹配"
usage_scenario:
    - "Spring Boot启动时报Could not resolve placeholder错误"
    - "配置文件节点名与@Value注解或属性注入占位符不一致"
    - "多环境配置间节点命名不统一导致占位符解析失败"
    - "校验yml键名与@ConfigurationProperties属性类字段是否匹配"
keywords:
    - "Spring Boot"
    - "占位符解析"
    - "application.yml"
    - "配置节点名"
source: "auto"
---

Spring Boot应用启动失败，报错 `Could not resolve placeholder 'mqtt.province.host'`，原因是代码中引用 `${mqtt.province.host}`，但 dev 配置文件 `application-dev.yml` 中使用的是 `mqtt.local` 节点名。需将 `mqtt.local` 改为 `mqtt.province` 以保持配置节点名与占位符一致。（来源：SearchReplace）
