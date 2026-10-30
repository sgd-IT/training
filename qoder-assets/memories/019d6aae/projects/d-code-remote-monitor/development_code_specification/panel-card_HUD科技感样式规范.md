---
title: ".panel-card HUD科技感样式规范"
usage_scenario: []
keywords:
    - "panel-card"
    - "HUD角标"
    - "全息扫描线"
    - "深蓝渐变"
    - "青色描边"
source: "user"
---

`.panel-card` 是园区大屏中深色半透明科技感面板的容器类，其样式规范如下：
- 背景：使用 `linear-gradient(170deg, rgba(14,44,78,0.70) 0%, rgba(10,36,66,0.65) 35%, rgba(8,30,56,0.70) 100%)` 深蓝灰半透明渐变；
- 边框：`1px solid rgba(79,195,247,0.1)` 极淡青色描边；
- 圆角：`border-radius: 8px`；
- 阴影：四层 `box-shadow`（外阴影+内发光）；
- 内边距：`padding: 16px 18px`；
- HUD角标：通过 `::before`（左上）和 `::after`（右下）伪元素实现，含2px青色描边与圆角及微光阴影；
- 全息扫描线：通过 `> .panel-scan` 子元素 + `panelScan` 动画实现，hover时激活。
