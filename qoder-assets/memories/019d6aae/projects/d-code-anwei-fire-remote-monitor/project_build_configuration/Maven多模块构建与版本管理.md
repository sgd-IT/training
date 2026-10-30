---
title: "Maven多模块构建与版本管理"
usage_scenario: []
keywords:
    - "flatten-maven-plugin"
    - "多模块"
    - "revision"
    - "server模块"
    - "BOM"
---

**Maven构建管理**：
- 使用 `flatten-maven-plugin 1.6.0` 统一管理多模块版本（`revision=1.0.0-jdk21`）
- `process-resources` 阶段执行 `flatten`，`clean` 阶段执行 `flatten.clean`
- 多模块结构：`server`（主服务）、`framework`（基础框架）、`dependencies`（BOM依赖管理）、`module-system`/`module-infra`/`module-eiot`/`module-ai`（功能拓展模块）
