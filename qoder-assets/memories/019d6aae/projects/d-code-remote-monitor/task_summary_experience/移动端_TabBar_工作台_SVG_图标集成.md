---
title: "移动端 TabBar 工作台 SVG 图标集成"
usage_scenario:
    - "在 uni-app 移动端项目中为任意 TabBar 项集成自定义 SVG 图标（含激活/非激活状态）"
    - "排查 TabBar 图标不显示、颜色异常或状态切换失效问题时参考实现方案"
keywords:
    - "TabBar"
    - "SVG图标"
    - "uni-app"
source: "auto"
---

## 任务描述
为移动端 TabBar 的‘工作台’Tab 替换为四宫格 SVG 图标，支持非激活（灰色 #999）与激活（红色 #c62828）双状态自动切换。

## 执行过程
```mermaid
graph TD
    A[需求:TabBar 工作台 SVG 图标] --> B[读取 TabBar.vue 确认现有结构]
    B --> C[创建 icon-workspace.svg（#999）]
    C --> D[创建 icon-workspace-active.svg（#c62828）]
    D --> E[修改 TabBar.vue 模板：添加 v-if/v-else 切换 image/uni-icons]
    E --> F[更新 TabBar.vue 数据项：为 workspace 添加 svg/svgActive 字段]
    F --> G[新增 .svg-icon 样式控制尺寸]
    G --> H[Read 验证最终文件一致性]
```

## 任务总结
成功实现 TabBar SVG 图标替换：创建两个 SVG 文件，改造 TabBar.vue 支持条件渲染与状态切换，新增 CSS 样式确保尺寸统一，其他 Tab 保持 uni-icons 不受影响。
