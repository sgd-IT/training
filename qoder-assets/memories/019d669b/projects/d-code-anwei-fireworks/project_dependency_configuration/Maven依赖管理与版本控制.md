---
title: "Maven依赖管理与版本控制"
usage_scenario: []
keywords:
    - "dependencyManagement"
    - "BOM"
    - "Spring Cloud Greenwich"
    - "多模块"
---

**依赖管理机制**: 
- 使用`<dependencyManagement>`统一声明版本，含Spring Boot BOM（2.1.5.RELEASE）、Spring Cloud Greenwich.SR6、Platform Cairo-SR7
- 关键依赖显式指定版本：FastJSON 1.2.70、MyBatis-Plus 3.2.0、Lombok 1.18.30、MySQL Connector/J 9.1.0等
- 存在本地模块替换需求（虽未写明路径，但`<modules>`结构暗示多模块依赖关系）
