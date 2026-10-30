---
title: "Maven依赖管理与模块结构"
usage_scenario:
    - "理解项目依赖组织方式与模块职责划分"
    - "新增模块或引入新依赖时遵循统一版本策略"
keywords:
    - "Maven BOM"
    - "ruoyi-common-bom"
    - "模块化"
    - "maven-compiler-plugin"
---

依赖管理方式：Maven BOM（Bill of Materials）统一版本管理。

关键BOM依赖：
- `spring-boot-dependencies`（v4.1.0）
- `sa-token-bom`（v1.45.0）
- `hutool-bom`（v5.8.47）
- `ruoyi-common-bom`（v6.0.0-BETA）

模块化结构：
- `ruoyi-admin`（Web入口）
- `ruoyi-common`（通用模块）
- `ruoyi-extend`（扩展模块）
- `ruoyi-modules`（业务模块）
- `ruoyi-api`（API模块）

构建特性：使用 Maven Compiler Plugin（v3.15.0），启用 `-release 21` 编译选项，确保JDK 21兼容性。
