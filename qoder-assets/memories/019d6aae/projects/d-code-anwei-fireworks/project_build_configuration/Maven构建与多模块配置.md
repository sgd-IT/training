---
title: "Maven构建与多模块配置"
usage_scenario:
    - "执行编译、打包或运行测试时参考构建命令和插件配置"
keywords:
    - "Maven"
    - "多模块"
    - "skipTests"
    - "Java 8"
source: "init"
---

Maven构建配置：
- 编译插件：maven-compiler-plugin 2.3.2 (source/target 1.8, UTF-8编码)
- 测试插件：maven-surefire-plugin 3.0.0-M5 (默认跳过单元测试 skipTests=true)
- 多模块结构：父工程包含 monkey-code-generator, monkey-common, monkey-security-core, monkey-service, monkey-admin-core, monkey-admin-api, monkey-monitor, monkey-monitor-api, xxl-job-admin, xxl-job-core 共10个子模块
