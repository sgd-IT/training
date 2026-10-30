---
title: "Maven构建插件配置"
usage_scenario:
    - "了解项目编译目标Java版本及测试执行策略"
keywords:
    - "maven-compiler-plugin"
    - "Java 8"
    - "跳过测试"
    - "父POM构建"
---

Maven构建配置：
- `maven-compiler-plugin`：强制源码与目标字节码为Java 8（source/target=1.8）
- `maven-surefire-plugin`：默认跳过单元测试（skipTests=true）
- 构建过程由父POM统一控制，子模块继承该配置
