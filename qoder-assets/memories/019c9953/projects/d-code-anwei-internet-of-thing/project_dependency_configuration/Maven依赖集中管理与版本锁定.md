---
title: "Maven依赖集中管理与版本锁定"
usage_scenario:
    - "了解项目如何统一管理多模块依赖版本及处理第三方SDK冲突"
keywords:
    - "dependencyManagement"
    - "BOM"
    - "版本锁定"
    - "Activiti排除MyBatis"
---

依赖管理采用Maven BOM模式：
- 通过`<dependencyManagement>`统一声明所有模块依赖版本（如Jackson 2.9.10.8修复安全漏洞）
- 多个云厂商SDK（七牛、阿里、腾讯）和支付SDK（微信、支付宝）均在此集中管控
- Activiti 7.1.0.M4工作流引擎被显式引入并排除冲突的MyBatis
