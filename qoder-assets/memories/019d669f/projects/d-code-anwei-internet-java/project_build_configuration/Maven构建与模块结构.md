---
title: "Maven构建与模块结构"
usage_scenario:
    - "执行编译、打包或了解项目模块划分"
keywords:
    - "Maven"
    - "多模块"
    - "编译器配置"
    - "跳过测试"
source: "init"
---

构建配置：
- 父工程packaging为pom，包含6个子模块：monkey-code-generator, monkey-common, monkey-service, monkey-monitor, monkey-monitor-api, monkey-mqtt-demo。
- Maven Compiler Plugin: 源码和目标版本1.8，编码UTF-8。
- Maven Surefire Plugin: 默认跳过单元测试 (skipTests=true)。
