---
title: "Maven构建插件配置"
usage_scenario: []
keywords:
    - "Maven"
    - "maven-compiler-plugin"
    - "skipTests"
    - "JDK 8"
---

**Maven构建配置**: 
- `maven-compiler-plugin 3.11.0`: 源码与目标字节码级别均为`1.8`, 编码UTF-8
- `maven-surefire-plugin 3.0.0-M5`: 默认跳过单元测试（`skipTests=true`）
