---
title: "Maven构建与模块配置"
usage_scenario:
    - "了解项目的构建命令、编译设置及模块划分"
keywords:
    - "Maven"
    - "Java 8"
    - "skipTests"
    - "多模块"
source: "init"
---

构建工具: Maven
编译配置: maven-compiler-plugin 2.3.2, 源码和目标版本均为 1.8, 编码 UTF-8
测试配置: maven-surefire-plugin 3.0.0-M5, 默认跳过单元测试 (skipTests=true)
模块结构: 父工程 anwei-internet-of-thing 下包含 monkey-code-generator, monkey-common, monkey-security-core, monkey-service, monkey-admin-core, monkey-admin-api, monkey-monitor, monkey-monitor-api, xxl-job-admin, xxl-job-core 等子模块
