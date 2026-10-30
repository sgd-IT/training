---
title: "前端仓库为pnpm布局，传递依赖无顶层软链需从.pnpm定位"
usage_scenario:
    - "编写Node脚本require前端仓库内包时"
    - "排查node_modules找不到依赖的MODULE_NOT_FOUND报错时"
keywords:
    - "pnpm"
    - ".pnpm目录"
    - "传递依赖"
    - "@vue/compiler-sfc"
    - "MODULE_NOT_FOUND"
source: "auto"
---

anwei-fire-remote-monitor-vue 前端仓库使用 pnpm 安装依赖（存在 pnpm-lock.yaml 与 pnpm-workspace.yaml）。实际验证：node_modules 下没有 @vue/compiler-sfc 的顶层软链接（它是 vue 3.5.12 的传递依赖），因此以绝对路径 require('.../node_modules/@vue/compiler-sfc') 或经 createRequire 从包根解析均会 MODULE_NOT_FOUND；真实包位于 node_modules/.pnpm/@vue+compiler-sfc@3.5.12/node_modules/@vue/compiler-sfc。在脚本中加载该包的可靠方式是 fs.readdirSync 扫描 .pnpm 目录并以 '@vue+compiler-sfc@' 前缀匹配后拼接绝对路径 require。同类传递依赖（如 @vue/* 内部包）在此仓库都遵循此规律，不能假设可从顶层 node_modules 直接引入。
