---
title: "Maven构建插件配置"
usage_scenario:
    - "了解项目编译目标Java版本、是否启用单元测试及父工程结构"
keywords:
    - "maven-compiler-plugin"
    - "Java 8"
    - "跳过测试"
    - "父工程"
    - "子模块"
---

Maven构建配置：
- 编译插件：`maven-compiler-plugin 2.3.2`，强制`source=1.8`、`target=1.8`
- 测试插件：`maven-surefire-plugin`默认`skipTests=true`（跳过单元测试）
- 工程类型：`pom`（父工程，含13个子模块）
