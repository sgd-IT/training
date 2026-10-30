---
title: "数据中心ECharts图表容器尺寸修复"
usage_scenario:
    - "Vue项目中ECharts图表空白且控制台报'Can't get DOM width or height'时的快速修复"
    - "在flex布局容器中使用ECharts时确保容器尺寸正确的通用方案"
    - "排查类似'图表不显示但无JS错误'问题的标准化诊断流程"
keywords:
    - "ECharts"
    - "容器尺寸"
    - "flex布局"
    - "图表不显示"
source: "auto"
---

## 任务描述
解决数据中心各分析图表（巡检/告警/设备/值班/工单）不显示的问题，根因为ECharts初始化时容器宽高为0。

## 执行过程
```mermaid
graph TD
    A[问题：图表空白] --> B[分析Console报错：Can't get DOM width or height]
    B --> C[检查所有分析组件及ChartCard.vue布局结构]
    D[定位共性：.chart-fill使用height: 100%在flex-column容器中失效]
    E[方案：统一改为flex: 1确保占满可用空间]
    F[批量修改Alarm/Device/Duty/Patrol/WorkOrderAnalysis.vue]
    G[验证修复后ECharts正常初始化渲染]
```

## 任务总结
根本原因是flex布局下`height: 100%`无法正确计算高度，导致ECharts获取到0尺寸容器。通过将5个分析组件中`.chart-fill`的`height: 100%`统一替换为`flex: 1`，彻底解决图表不显示问题，修复后ECharts可正常获取容器尺寸并渲染。

## Notes
- 本记忆原方案（仅把 height:100% 改 flex:1）不充分：数据中心重规划轮经用户评审指出，flex 链上只要有一层未设 min-width:0，width:100% 仍会被撑爆或缩成 0——这正是当年进一步移除 ECharts 的根因之一。
- 最新完整规避口径：沿父容器链每一层同时补 min-width:0 + min-height:0；图表容器一律用行内 style（绕开 scoped 样式穿透/:slotted() 失效问题）；初始化链固定 onMounted → nextTick → echarts.init → setOption；极端情况退路是给容器直接固定像素 height。仅靠 flex:1 单一改动不足以根治。
