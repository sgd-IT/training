---
title: "Maven依赖版本管理"
usage_scenario:
    - "添加新依赖或升级现有依赖时参考版本管理策略"
keywords:
    - "dependencyManagement"
    - "版本控制"
    - "Spring Cloud"
    - "依赖排除"
source: "init"
---

依赖管理配置：
- 使用dependencyManagement统一管理版本。
- 引入spring-boot-dependencies和platform-bom进行版本对齐。
- 关键版本变量定义在properties中，如fastjson.version, mybatisplus.version等。
- Activiti依赖排除了mybatis以避免冲突。
- Jackson升级至2.9.10.8以修复安全漏洞。
