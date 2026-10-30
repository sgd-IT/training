---
title: "移动端UI主题色统一与统计页重构任务归档"
usage_scenario:
    - "移动端告警中心页面UI一致性改造时复用.chip.active标签样式规范"
    - "排查告警中心筛选区域激活态视觉反馈不明显问题时快速定位修复方案"
keywords:
    - "UI主题色"
    - "渐变"
    - "灵动岛适配"
    - "统计页面"
    - "safe-area-top"
    - "NavBar"
    - "TabBar"
    - "胶囊按钮"
    - "垂直居中"
    - "对齐"
    - "statusBarHeight"
    - "navBarHeight"
    - "告警中心"
    - "chips"
    - "chip.active"
source: "auto"
---

## Task description
- Core requirement: 统一移动端UI主题色为#1e39b1并重构统计页面
- Task background: 首页header原为红色渐变；.type-svg图标尺寸过大；告警中心tab-item.active等状态色不一致；统计页面需按设计图重写并适配灵动岛等特殊机型

## Execution process
1. pages/index/index.vue：将.header背景从#C62828→#8E0000红色渐变替换为#3D5BD9→#1E39B1→#132A7E蓝色渐变，并添加右上角光晕效果
2. pages/index/index.vue：将.type-svg图标尺寸从64rpx缩小至44rpx（视觉10px）
3. pages/alarm-center/index.vue：将.tab-item.active文字色、下划线、.cat-tag.active背景/文字色、.btn-pri按钮背景统一改为#1e39b1主题色
4. pages/stats/index.vue：完全重写为响应式统计页面，包含自定义标题栏、3个KPI卡片、CSS折线图、柱状图及导出按钮，并新增.safe-area-top类适配灵动岛/刘海屏

## Related files
- /pages/index/index.vue
- /pages/alarm-center/index.vue
- /pages/stats/index.vue

## Notes
- 本次任务进一步优化了NavBar组件与TabBar组件的样式与交互逻辑，确保标题与胶囊按钮垂直居中对齐，并统一了TabBar激活态颜色为#1e39b1。
- 告警中心页面的.chips容器和.chip.active标签样式在本任务中进行了新的微调优化，以实现更清晰的视觉层次和选中反馈。
