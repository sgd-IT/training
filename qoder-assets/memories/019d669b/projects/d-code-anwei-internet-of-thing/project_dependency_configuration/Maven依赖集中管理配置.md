---
title: "Maven依赖集中管理配置"
usage_scenario: []
keywords:
    - "dependencyManagement"
    - "版本统一"
    - "jackson-databind"
    - "Spring Boot BOM"
---

**依赖管理机制**：
- 使用`<dependencyManagement>`统一声明所有模块依赖版本
- 关键覆盖项：`jackson-databind 2.9.10.8`（修复安全漏洞）、`spring-boot-dependencies 2.1.5.RELEASE`、`platform-bom Cairo-SR7`、`spring-cloud-dependencies Greenwich.SR6`
- 各模块（如monkey-common、xxl-job-core等）继承父POM版本约束
