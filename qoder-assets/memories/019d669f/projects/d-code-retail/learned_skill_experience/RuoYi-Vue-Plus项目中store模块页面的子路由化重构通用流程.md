---
title: "RuoYi-Vue-Plus项目中store模块页面的子路由化重构通用流程"
usage_scenario:
    - "当需要对store/retailInfo、store/person或store/inventory等页面进行模块化拆分以提高可维护性"
    - "当需要为现有store模块页面添加独立的、可直接访问的子功能入口"
keywords:
    - "前端重构"
    - "子路由"
    - "Vue Router"
    - "RuoYi-Vue-Plus"
    - "CSS Grid"
    - "响应式布局"
source: "auto"
---

## Goal
将一个单页应用（SPA）中的功能模块拆分为多个独立的、可路由的子组件，同时保持共享UI元素和响应式布局，适用于Vue3 + RuoYi-Vue-Plus项目。

## Steps
Step 1: 文件创建 - 创建新的子组件文件（如 records.vue 和 overview.vue），分别封装各自的功能逻辑（数据表格/筛选器 和 图表/统计面板）， rationale: 新组件必须先存在才能被路由引用，input: 功能需求描述，output: 新的 .vue 文件
Step 2: 父组件重构 - 修改原父组件（index.vue），移除内联业务逻辑，保留共享UI（指标卡片、预警条、标签导航），并添加 `<router-view>` 占位符， rationale: 父组件作为布局容器，其职责应仅限于展示共享内容和渲染子路由，input: 原 index.vue 内容，output: 精简后的 index.vue
Step 3: 路由注入 - 在权限管理模块（permission.ts）中，为动态加载的父路由（如 inventory）注入子路由配置，并设置默认重定向， rationale: Vue Router 的嵌套路由需要在路由注册时就定义父子关系，否则无法正确匹配和渲染，input: 父路由路径和子路由信息，output: rewriteRoutes 数组更新
Step 4: 布局调整 - 修改子组件（overview.vue）的CSS，使用 Grid 或 Flexbox 将多个区块（图表、统计面板）从垂直堆叠改为水平排列，并添加响应式断点， rationale: 水平布局能更高效地利用屏幕空间，提升仪表盘的可读性，input: 当前CSS样式，output: 更新后的CSS类
Step 5: Phase check - 确认所有文件已创建、父组件已重构、路由已注入、布局已调整；当子组件数量超过2个时，需额外验证路由命名是否唯一且语义清晰；当目标是移动端优先时，需将主断点调整为 ≤768px 并增加触摸友好性优化
