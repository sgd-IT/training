---
title: "销售闭环 SQL 切换菜单 component 致列表页功能入口遗漏"
usage_scenario:
    - "排查列表页功能入口缺失"
    - "菜单 component 被升级脚本切换后核对功能"
    - "对齐销售闭环新旧页面完整度"
keywords:
    - "SalesClueListPage 详情入口"
    - "system_menu component 迁移"
    - "CrmClueDetail 跳转"
    - "销售闭环页面完整度"
    - "菜单升级脚本"
source: "auto"
---

Bug 类：销售闭环迁移后列表页丢失详情入口，用户「找不到线索详情」。根因：多轮 SQL 升级脚本（crm_sales_clue_page.sql、crm_sales_clue_list_v2.sql）反复改 system_menu.component，把线索菜单 2404 从 crm/clue/index → crm/clue/SalesCluePage → crm/clue/SalesClueListPage 逐版切换，最新页面未平移旧页的详情入口（旧页线索名称是可跳 CrmClueDetail 的 el-link）。实际库中菜单 2404 挂在 2397（/crm）下、path=clue，详情页路由 activeMenu='/crm/clue' 匹配正确。修复模式：在 SalesClueListPage.vue 名称列改 el-link、操作列补「详情」按钮、新增 openDetail 用 router.push({name:'CrmClueDetail'})，与 SalesCustomerPage.vue 完整度对齐。复用教训：用 SQL 脚本切换菜单 component 做页面迁移时，必须对照被替换页面逐项核对功能入口，销售闭环页面以 SalesCustomerPage.vue 为完整度基准。
