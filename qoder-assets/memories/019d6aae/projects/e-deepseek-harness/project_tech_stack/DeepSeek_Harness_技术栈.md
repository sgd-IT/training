---
title: "DeepSeek Harness 技术栈"
usage_scenario:
    - "了解项目使用的编程语言、包管理器、核心框架及架构模式"
keywords:
    - "TypeScript"
    - "pnpm"
    - "Cordis"
    - "React"
    - "Monorepo"
source: "init"
---

开发语言：TypeScript
包管理器：pnpm (版本 11.7.0)
运行时环境：Node.js ^22.19.0 || >=24.0.0
核心框架与库：
- Cordis: 驱动架构 (Spatiotemporal Composability)
- React: Web UI 组件库
- Vitest: 测试框架
- Oxlint: 代码检查工具
- Vite: 构建工具
架构模式：Monorepo (Workspaces)，一切皆插件
