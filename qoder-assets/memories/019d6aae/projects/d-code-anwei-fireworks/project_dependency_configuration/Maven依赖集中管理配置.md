---
title: "Maven依赖集中管理配置"
usage_scenario: []
keywords:
    - "dependencyManagement"
    - "版本集中管理"
    - "Jackson加固"
    - "Spring Cloud版本"
---

**依赖管理机制**：
- 通过`<dependencyManagement>`统一声明版本，子模块按需引入
- 关键版本控制：
  - Jackson Databind `2.9.10.8`（安全加固）
  - Spring Boot Dependencies `2.1.5.RELEASE`（定制化基础依赖）
  - Spring Cloud `Greenwich.SR6`（对应Boot 2.1.x）
- 私有/常用库版本集中定义在`<properties>`中（如fastjson、easyexcel等）
