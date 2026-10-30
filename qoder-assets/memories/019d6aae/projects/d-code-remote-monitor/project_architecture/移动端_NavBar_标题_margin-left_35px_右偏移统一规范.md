---
title: "移动端 NavBar 标题 margin-left 35px 右偏移统一规范"
usage_scenario:
    - "新增或改造带自定义NavBar的移动端页面"
    - "走查多页标题位置不一致时校准"
keywords:
    - "aw-navbar__title-wrap"
    - "margin-left 35px"
    - "NavBar标题对齐"
    - "移动端页面一致性"
source: "auto"
---

anwei-fire-remote-monitor-mobile 项目内使用自定义 NavBar 的页面，统一用同一条 CSS 让标题从居中位右移，避开左侧图标区：`::v-deep .aw-navbar__title-wrap.is-center { margin-left: 35px; }`。已在 alarm-center/index.vue、duty/index.vue、messages/index.vue 三页核实一致。

约定要点：一律用 margin-left 正偏移而非 margin-right 或改 text-align，以保证各页标题起始位置完全对齐；新增带 NavBar 的页面须沿用同一数值 35px，不要按单页视觉微调，否则会破坏跨页一致性并在后续 UI 走查中被反复返工。该规则只影响标题容器，与 NavBar 组件自身的 theme/border 属性相互独立。
