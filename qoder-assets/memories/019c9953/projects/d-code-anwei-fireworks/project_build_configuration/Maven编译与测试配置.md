---
title: "Maven编译与测试配置"
usage_scenario:
    - "了解项目编译目标JDK版本及测试执行策略"
keywords:
    - "Maven"
    - "Java 8"
    - "maven-compiler-plugin"
    - "跳过测试"
---

Maven构建配置：
- 编译插件：maven-compiler-plugin 2.3.2，强制源码与目标字节码为Java 8（source/target=1.8）
- 测试插件：maven-surefire-plugin 3.0.0-M5，默认跳过单元测试（skipTests=true）
