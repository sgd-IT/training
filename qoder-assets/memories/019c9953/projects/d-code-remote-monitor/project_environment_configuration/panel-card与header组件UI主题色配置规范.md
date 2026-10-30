---
title: "panel-card与header组件UI主题色配置规范"
usage_scenario:
    - "调整UI主题色或修复背景透出问题时复用该渐变值"
    - "新页面组件需保持一致视觉风格时参考此配色"
    - "审查UI渲染效果是否符合设计预期"
keywords:
    - "panel-card"
    - "header"
    - "线性渐变"
    - "rgba背景"
    - "UI主题色"
source: "auto"
---

header 和 panel-card 元素使用线性渐变背景：`background: linear-gradient(170deg, rgb(18, 37, 67) 0%, rgb(18, 35, 61) 35%, rgb(21, 44, 79) 100%)`，该配色完全不透明且色调偏亮蓝，确保地图内容不透出。

panel-card组件的背景色值设置为rgba(10, 26, 48, 0.3)。注意：此配置仅控制背景色值，不控制透明度；实际渲染中需结合父容器样式确保视觉效果符合设计预期。
