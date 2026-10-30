---
title: "anwei-inner 前后端双 Git 仓库结构与远程地址"
usage_scenario:
    - "拉取或提交 anwei-inner 代码时确认仓库范围"
    - "定位销售闭环页面升级 SQL 脚本位置"
    - "判断改动属于前端仓库还是后端仓库"
keywords:
    - "双Git仓库"
    - "anwei-inner-office-vue"
    - "内网Git远程地址"
    - "master分支"
    - "sql/mysql 升级脚本"
source: "auto"
---

anwei-inner 工作区（d:\code\anwei-inner）由两个相互独立的 Git 仓库组成：后端 anwei-inner-office（yudao 框架多模块 Maven 工程）与前端 anwei-inner-office-vue（pnpm + Vite），二者均长期在 master 分支开发，需分别执行 git fetch / pull / commit，不存在单仓联动。远程主机为内网 Git `192.168.250.252:10102`。前端页面级 SQL 升级脚本集中在 anwei-inner-office\sql\mysql 目录（如 crm_sales_clue_list_v2.sql、crm_sales_menu_order_upgrade.sql），排查页面差异时需在该目录按菜单 id 追溯 component 的多轮变更。

## Git Push Details
- To push changes: `git push origin master`
- Before pushing, verify the local branch is ahead of remote: `git status -sb` shows `ahead N`.
