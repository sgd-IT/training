---
title: "大屏Teleport浮层的今日警情卡锚点与逐屏偏移推算"
usage_scenario:
    - "为大屏Teleport浮层计算定位偏移数值"
    - "在两屏间同步调整同一浮层的位置"
    - "评估新浮层能否复用既有锚点数值"
keywords:
    - "今日警情卡位置"
    - "Teleport浮层偏移推算"
    - "right-panel宽400"
    - "showPanels显隐"
    - "顶部锚定模式"
source: "auto"
---

消防值守 PC 两屏的"今日警情"卡片位置与浮层锚点几何（实测 CSS 值，用于 body 级 Teleport 浮层的偏移推算）：

- duty-screen（值守大屏）：顶栏 `.header` 高 56px；内容区 `.main-content` padding 10px、gap 10px；右栏 `.right-panel` 宽 400px、margin-left auto、内部 gap 8px，可被页面级开关 showPanels 整体隐藏，卡片顺序为 今日警情 → 实时告警 → 告警趋势。由此推算：右栏第一张卡（今日警情）顶边距视口顶部 = 56 + 10 = **66px**；其左边缘距视口右侧 = 400 + 10 = **410px**。故贴该卡左上角外侧且留 4px 缝的浮层应取 offset-top=66、offset-right=414。
- monitoring-center（监控中心大屏）：今日警情卡在**左栏 `.col-left` 的最上方**，卡左边缘距屏幕仅约 10px，外侧没有空间容纳椭圆入口类浮层——需要贴该卡的浮层只能改挂到卡的右上角内侧或换锚到其它卡片，不能照搬 duty-screen 的外侧左对齐做法。

结构性约束：两屏都有"今日警情"卡，但分属左右不同栏，任何按视觉位置推算 offset-left/offset-right 的大屏浮层必须逐屏单独取值，不能用同一组数字套用两屏；且这类浮层须独立于 `.right-panel` 的整体显隐开关（showPanels），否则右栏收起时入口一并消失。两屏均存在的"告警趋势"卡片可作为右下角之外的稳定锚点参照物。alarm-notice 组件通过 offset-top prop 支持"顶部锚定"模式（面板向下展开、动画 transform-origin 改 top center），未设定则保持底部锚定行为，以便两屏各自选择锚点方向。
