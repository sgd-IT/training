---
title: "pnpm 依赖与工作空间配置"
usage_scenario:
    - "了解项目的依赖管理策略、工作空间结构及特殊依赖处理"
keywords:
    - "pnpm"
    - "workspaces"
    - "overrides"
    - "patched dependencies"
source: "init"
---

依赖管理配置：
- 包管理器：pnpm (lockfileVersion 9.0)
- 工作空间 (Workspaces): vendor/*, packages/*/*, apps/*, website 等
- 本地模块替换 (Overrides):
  - `@deepseek-ai/cosmokit` → link:vendor/cosmokit
  - `@deepseek-ai/schemastery` → link:vendor/schemastery
- 补丁依赖 (Patched Dependencies):
  - `node-pty@1.2.0-beta.15`: 应用了特定 patch
- 预安装钩子：postinstall 执行 `scripts/install-lefthook.mjs`
