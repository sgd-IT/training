---
title: "duty-screen 弹窗背景分层：$glass-bg 与 alarm-popup-overlay 遮罩模糊"
usage_scenario:
    - "调整大屏报警弹窗外侧背景模糊或暗度时"
    - "修改 duty-screen 弹窗卡片玻璃底色透明度时"
    - "区分弹窗遮罩与其他组件 backdrop-filter 归属时"
keywords:
    - "$glass-bg"
    - "alarm-popup-overlay"
    - "backdrop-filter"
    - "弹窗遮罩暗度"
    - "duty-screen样式分层"
source: "auto"
---

duty-screen（/src/views/eiot/firewatch/duty-screen/index.vue）弹窗视觉分两层，改动前先确认目标层：

1. 弹窗"外侧背景"的模糊与压暗由 `.alarm-popup-overlay` 遮罩层控制，不是 $glass-bg。原值为 `background: rgba(0, 0, 0, 0.65)` + `backdrop-filter: blur(4px)`；2026-10 按用户要求调整为 `rgba(0, 0, 0, 0.4)` + `blur(1px)`，monitoring-center/index.vue 中同名 overlay 保持同值同步修改。只降 blur 而不动遮罩暗度时感知不明显，两者需一起调。

2. `$glass-bg` SCSS 变量用于 duty-screen 弹窗卡片自身背景，当前值为 rgba(10, 22, 40, 0.001)，近乎完全透明；定义在同文件样式块中。调整弹窗卡片玻璃底色/透明度时改这个变量。

3. 该文件内其余 backdrop-filter 出现在下拉菜单等组件上（如 blur(8px)/blur(16px)），与报警弹窗无关，定位时须按选择器区分。
