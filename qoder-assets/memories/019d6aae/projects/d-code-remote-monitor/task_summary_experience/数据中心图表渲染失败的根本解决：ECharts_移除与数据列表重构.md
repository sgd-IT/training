---
title: "数据中心图表渲染失败的根本解决：ECharts 移除与数据列表重构"
usage_scenario:
    - "Vue 3 中因 slot 作用域导致图表尺寸获取失败的同类问题"
    - "需快速降级图表功能为数据列表以保障交付时的参考方案"
    - "排查 scoped 样式与 :slotted() 在复杂嵌套组件中失效的典型场景"
keywords:
    - "Vue 3 slot"
    - "ECharts 移除"
    - "数据列表"
source: "auto"
---

## 任务描述
解决数据中心图表因 Vue 3 slot 作用域机制导致的 'Can't get DOM width or height' 渲染失败问题，最终方案为彻底移除 ECharts，改用轻量数据列表展示。

## 执行过程
```mermaid
graph TD
    A[问题:图表空白+控制台报错] --> B[根因分析:Vue 3 slot 不继承子组件 scope attribute]
    B --> C[尝试CSS方案: :slotted\(.chart-fill\) + flex布局 → 失败]
    C --> D[尝试JS方案: sizeChart\(\) 设置像素宽高 → 仍不稳定]
    D --> E[最终决策:放弃图表，改用数据列表]
    E --> F[重写5个分析组件:Alarm/Device/Duty/Patrol/WorkOrderAnalysis.vue]
    F --> G[清理ChartCard.vue中冗余chart样式和slot包裹]
    G --> H[全局grep确认echarts引用仅存于其他模块，数据中心已清空]
```

## 任务总结
彻底移除数据中心所有 ECharts 依赖，5个分析组件全部重构为响应式数据列表，保留 API 加载、Mock 兜底、筛选联动等核心逻辑，显著提升首屏加载速度与稳定性。

## Notes
- "最终方案为彻底移除 ECharts / 数据列表"是当时的阶段性结论，已被数据中心重规划轮推翻：本轮决定重新引入 ECharts 图表化，不再维持纯列表形态。
- 本记忆真正的可复用价值在于根因诊断（Vue 3 slot 不继承子组件 scope attribute，导致 scoped 样式/:slotted() 穿透失效、容器宽高为 0），而非"必须用列表"的取舍。
- 新一轮规避口径：图表容器一律行内 style（绕开 scoped 穿透）+ 沿 flex 父链每层补 min-width:0/min-height:0 + onMounted→nextTick→init→setOption + useChart composable 收口生命周期；loading 仅首屏以避免 slot DOM 重建引入实例竞态。这些正是针对本记忆所列失败原因给出的对症解法。
