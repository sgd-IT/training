---
title: "Maven构建与依赖管理"
usage_scenario:
    - "了解项目Maven构建流程、模块结构及测试执行策略"
keywords:
    - "Maven"
    - "多模块"
    - "跳过测试"
    - "依赖管理"
source: "init"
---

Maven构建配置：
- 父工程模块：monkey-code-generator, monkey-common, monkey-service, monkey-monitor, monkey-monitor-api, monkey-mqtt-demo
- 编译器配置：source/target 1.8, UTF-8编码
- 测试配置：maven-surefire-plugin跳过单元测试 (skipTests=true)
- 依赖管理：使用dependencyManagement统一控制版本，包括Jackson安全升级至2.9.10.8
