---
title: "pnpm 工作区依赖配置"
usage_scenario:
    - "了解项目的依赖管理和工作区结构"
keywords:
    - "pnpm"
    - "workspaces"
    - "dependency management"
source: "init"
---

包管理器：pnpm (版本 11.7.0)
工作区配置：
- vendor/*
- packages/*/*
- native/landlock-run
- apps/*
- website

依赖管理：
- 使用 workspace:^ 引用本地包
- 私有仓库 (private: true)
