---
title: "Vue测试页路径与路由注册约定"
usage_scenario:
    - "在Vue项目中快速创建并注册一个临时测试页面"
    - "为新功能验证新建独立空白页面并确保可通过URL访问"
    - "统一团队内测试页面的目录结构与路由命名规范"
keywords:
    - "Vue测试页"
    - "路由注册"
    - "remaining.ts"
    - "src/test"
source: "auto"
---

## 决策场景
在Vue项目中为测试目的新建空页面时确定路径与路由配置方式

## 决策内容
1. 页面存放路径：`src/test/{name}/index.vue`
2. 路由路径：`/test/{name}`
3. 路由配置位置：`src/router/modules/remaining.ts`
4. 默认隐藏标签页（`hidden: true, noTagsView: true`）

## 适用范围
anwei-fire-remote-monitor-vue 及同架构 Vue 3 + Pinia + Vue Router 项目中的测试页面创建
