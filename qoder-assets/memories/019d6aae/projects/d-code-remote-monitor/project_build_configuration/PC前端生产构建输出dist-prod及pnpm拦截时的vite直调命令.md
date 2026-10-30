---
title: "PC前端生产构建输出dist-prod及pnpm拦截时的vite直调命令"
usage_scenario:
    - "构建PC管理台生产包时"
    - "pnpm build:prod报依赖脚本错误时"
    - "定位PC前端构建产物目录时"
keywords:
    - "VITE_OUT_DIR"
    - "dist-prod输出目录"
    - "ERR_PNPM_IGNORED_BUILDS"
    - "vite build --mode prod"
    - "nginx静态部署"
source: "auto"
---

PC 前端（anwei-fire-remote-monitor-vue）生产构建输出目录由 .env.prod 的 VITE_OUT_DIR=dist-prod 指定（本地源码目录中的 dist/ 是 2026-07 手放的 favicon/images/static 静态残留，不是 vite 产物，不可当作构建结果使用）。标准命令 pnpm build:prod 在本机会被 pnpm 的 supply-chain 检查触发自动 install 并以 [ERR_PNPM_IGNORED_BUILDS]（esbuild、@swc/core 等 ignored build scripts）报错退出；可用的等价绕行命令为在仓库根执行 node --max_old_space_size=4096 ./node_modules/vite/bin/vite.js build --mode prod，输出 Build successful 并生成 dist-prod。PowerShell 管道下该命令 exit code 可能为 1，属 Select-Object 假象，以日志文本为准。部署时将 dist-prod 整体覆盖到 18080 nginx 的前台静态目录。
