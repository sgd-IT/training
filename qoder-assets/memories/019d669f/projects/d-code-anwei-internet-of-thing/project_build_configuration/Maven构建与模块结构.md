---
title: "Maven构建与模块结构"
usage_scenario:
    - "执行编译、打包或理解多模块结构时参考"
keywords:
    - "Maven"
    - "多模块"
    - "跳过测试"
    - "Java 8"
source: "init"
---

Maven构建配置：
- 编译器插件：maven-compiler-plugin 2.3.2 (source/target 1.8, UTF-8编码)
- 测试插件：maven-surefire-plugin 2.22.2 (默认跳过单元测试 skipTests=true)
- 打包方式：pom (多模块父工程)
- 模块列表：monkey-code-generator, monkey-common, monkey-security-core, monkey-service, monkey-admin-core, monkey-admin-api, monkey-monitor, monkey-monitor-api, monkey-province, xxl-job-core, xxl-job-admin, monkey-province-gz, monkey-province-normal
