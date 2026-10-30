---
title: "告警模块SVG图标标准化替换流程"
usage_scenario:
    - "新增或替换任意告警类型图标（如后续增加‘测试’‘误报’等）"
    - "将其他页面中的 emoji 或 iconfont 替换为 SVG 时参考统一模式"
    - "排查 SVG 不显示、颜色错误或尺寸异常问题"
keywords:
    - "SVG替换"
    - "图标标准化"
    - "告警类型"
source: "auto"
---

## 任务描述
将告警类型分布（监管、屏蔽）和告警中心位置图标，从旧有 iconfont/emoji 替换为自定义 SVG 图标，实现矢量、可配色、高一致性显示。

## 执行过程
```mermaid
graph TD
    A[接收SVG源码] --> B[创建/static/icon-xxx.svg文件]
    B --> C[定位Vue组件中typeList或location字段]
    C --> D[将icon: 'xxx'或text: '📍'替换为svg: '/static/icon-xxx.svg']
    D --> E[如需image渲染，补充对应CSS样式（宽高、flex-shrink）]
    E --> F[验证颜色fill值与业务语义一致（如监管#1565c0、屏蔽#2e7d32、位置#666）]
```

## 任务总结
完成三处 SVG 替换：监管/屏蔽图标在 index.vue typeList 中由 icon 字段改为 svg 字段；位置图标在 alarm-center/index.vue 中由 emoji 改为 image 标签引用，并新增 .info-svg 样式确保尺寸与对齐。关键原则：SVG fill 值需匹配业务色值，路径统一用 /static/，Vue 中优先用 svg 字段（若无则增 image + class）。
