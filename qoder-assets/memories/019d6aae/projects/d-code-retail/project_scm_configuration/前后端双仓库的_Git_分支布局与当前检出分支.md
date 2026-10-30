---
title: "前后端双仓库的 Git 分支布局与当前检出分支"
usage_scenario:
    - "提交或推送前确认目标分支时"
    - "用户要求跨分支同步提交时"
    - "排查两仓库分支状态不一致时"
keywords:
    - "regulatory 分支"
    - "master 分支"
    - "双仓库结构"
    - "git branch -vv"
    - "远程推送同步"
source: "auto"
---

本项目为两个独立 Git 仓库：后端 `d:\code\retail\anwei-fireworks-retail`、前端 `d:\code\retail\anwei-fireworks-retail-admin`，各自有 origin 远程，需分别提交与推送。

后端仓库存在两条分支：`master`（主干）与 `regulatory`（监管功能分支），两者都有对应远程分支 origin/master、origin/regulatory。regulatory 由 master 线性延伸而来，日常开发（如监管统计接口、演示大屏配套改动）在 regulatory 分支上进行，本地默认检出分支常为 regulatory；用户会要求把同一提交同时体现在 master 上。

前端仓库只有 `master` 一条分支，所有前端改动（大屏、报警、视频页面等）均直接提交到 master。

因此提交/推送前必须先用 `git branch --show-current` 或 `git branch -vv` 确认各仓库当前检出分支与领先/落后状态，不能假设两仓库分支一致；推送需按分支显式指定（如 `git push origin regulatory`）。本地调测配置（后端 application-dev.yml、前端 .env.development 与 vite.config.ts）长期保持不提交状态。
