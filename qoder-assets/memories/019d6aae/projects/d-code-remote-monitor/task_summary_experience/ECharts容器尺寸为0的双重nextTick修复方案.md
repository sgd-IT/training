---
title: "ECharts容器尺寸为0的双重nextTick修复方案"
usage_scenario:
    - "ECharts初始化报clientWidth/clientHeight=0时的根因分析与修复"
    - "含v-if/v-else动态slot的子组件中初始化第三方图表库"
    - "Vue组合式API中等待多层嵌套DOM渲染完成的时序控制"
keywords:
    - "ECharts"
    - "nextTick"
    - "v-if渲染"
    - "容器尺寸"
source: "auto"
---

## 任务描述
解决 ECharts 初始化时报错 'Can\'t get DOM width or height'（clientWidth/clientHeight 为 0）的问题。

## 执行过程
```mermaid
graph TD
    A[现象: ECharts 报 clientWidth/clientHeight=0] --> B[第一轮修复: 修改 .chart-fill CSS]
    B --> C[发现仍报错 → 分析 ChartCard.vue 结构]
    C --> D[定位根因: v-if/v-else 切换需额外渲染周期]
    D --> E[第二轮修复: 在 watch/onMounted 中添加双重 await nextTick()]
    E --> F[第一个 nextTick: 等待父组件 DOM 更新]
    F --> G[第二个 nextTick: 等待子组件 v-else slot 渲染完成]
    G --> H[确保 .chart-fill 具备真实尺寸后调用 renderCharts()]
```

## 任务总结
成功修复 ECharts 尺寸为 0 问题：对 AlarmAnalysis、DeviceAnalysis、DutyAnalysis、PatrolAnalysis、WorkOrderAnalysis 5 个组件，在 watch 和 onMounted 的异步逻辑中均插入双重 await nextTick()，使 ECharts 初始化前能准确读取容器尺寸。
