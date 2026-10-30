---
title: "消息中心与告警中心UI主题色及去AI味设计规范"
usage_scenario:
    - "移动端消息中心与告警中心页面UI改造"
    - "移动端火警警示色与次要文字色选型"
    - "Code Review检查移动端UI是否偏离#1e39b1主题"
keywords:
    - "消息中心"
    - "告警中心"
    - "移动端主题色"
    - "#1e39b1"
    - "#d84315"
    - "chips容器"
    - "chip.active标签"
    - "PC大屏token区分"
source: "auto"
---

消息中心与告警中心页面UI设计必须遵循以下规范：
- 主题色统一：除火警/紧急消息外，所有UI元素（按钮、标签、图标、文字）统一使用主题色#1e39b1（移动端主色可搭配浅蓝#3d5bd9），次要文字使用#6b7484以保证对比度；火警消息使用红色#d84315作为警示色
- 去AI味：禁止使用彩虹渐变、脉冲动画、冗余结构和机械式命名；采用自然简洁的变量/方法名，精简条件分支，样式类名缩短合并，整体风格贴近真实人类开发者习惯
- 告警中心样式规范：.chips容器需添加padding-top: 10px；.chip.active标签需应用border-radius: 16px椭圆背景和#1e39b1主题色背景，文字颜色为#fff；滑动椭圆高亮块.hl需应用cubic-bezier(0.34, 1.2, 0.4, 1)弹性缓动、半透明背景var(--brand-100)、内描边和外阴影

## Notes
- 第三条中的滑动椭圆高亮块 .hl 在当前代码里已不存在：本轮直接读取 anwei-fire-remote-monitor-mobile/pages/alarm-center/index.vue 的样式块，筛选区只有 .tab-nav + .tab（含 .tab.active/.tab.disabled）与吸顶 .search-bar，无 .hl 绝对定位高亮块。主题色 #1e39b1/#d84315 与去 AI 味两条口径仍然有效，改动时以现状为准，不要按 .hl 方案复原或引用其 cubic-bezier 参数。
