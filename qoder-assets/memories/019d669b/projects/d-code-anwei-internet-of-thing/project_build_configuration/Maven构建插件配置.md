---
title: "Maven构建插件配置"
usage_scenario: []
keywords:
    - "Maven"
    - "compiler-plugin"
    - "JDK 8"
    - "跳过测试"
---

**Maven构建配置**：
- `maven-compiler-plugin 2.3.2`: 源码与目标字节码版本均为`1.8`，编码`UTF-8`
- `maven-surefire-plugin`: 默认跳过单元测试（`<skipTests>true</skipTests>`）
