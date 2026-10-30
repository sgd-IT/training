---
title: "anwei-inner-office-vue 依赖构建脚本白名单配置位置"
usage_scenario:
    - "处理前端依赖安装警告"
    - "新增依赖触发构建脚本提示"
    - "排查 Vite 依赖二进制问题"
keywords:
    - "allowBuilds"
    - "ERR_PNPM_IGNORED_BUILDS"
    - "approve-builds"
    - "esbuild"
    - "依赖 postinstall"
source: "auto"
---

anwei-inner-office-vue 前端使用 pnpm 11，依赖 postinstall 构建脚本的白名单声明在 anwei-inner-office-vue/pnpm-workspace.yaml 的 allowBuilds 字段（值 true/false）。pnpm 10+ 默认不执行依赖脚本，安装时会报 ERR_PNPM_IGNORED_BUILDS 并提示 approve-builds。当前配置：esbuild 与 @parcel/watcher 为 true（Vite 构建与文件监听必需），core-js-pure 与 es5-ext 为 false（仅输出赞助/提示信息）。修改后需重新执行 pnpm install 才会运行被允许的脚本。
