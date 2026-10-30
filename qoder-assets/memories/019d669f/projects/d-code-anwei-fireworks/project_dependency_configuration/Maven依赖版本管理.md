---
title: "Maven依赖版本管理"
usage_scenario:
    - "解决依赖冲突或添加新依赖时参考版本管理"
keywords:
    - "dependencyManagement"
    - "Spring Cloud"
    - "版本锁定"
    - "Jackson"
source: "init"
---

依赖管理策略: 使用 <dependencyManagement> 统一管理版本。
版本对齐: 
- Spring Boot 2.1.5.RELEASE (通过 spring-boot-dependencies import)
- Spring Cloud Greenwich.SR6 (通过 spring-cloud-dependencies import)
- Platform BOM Cairo-SR7
特定依赖版本锁定: Jackson 2.9.10.8, MySQL Connector 9.1.0, Groovy 4.0.24 等。
