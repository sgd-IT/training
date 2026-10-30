---
title: "报警弹窗图标与布局规范：禁emoji统一SVG、视频入口在信息区"
usage_scenario:
    - "修改报警弹窗 UI"
    - "新增弹窗图标或入口"
    - "两屏弹窗视觉核对"
keywords:
    - "SVG图标"
    - "弹窗UI口径"
    - "抓拍图片尺寸"
    - "查看视频入口"
source: "user"
---

用户明确要求：报警弹窗内禁止用 emoji 表情替代图标。弹窗原有 ⚠/🔊/🔇/▶/⏸/📹/🎥/📋/📟/📍/🕐/📝 等 emoji 已全部替换为内联 SVG（viewBox 0 0 24 24、描边风格、stroke-width 1.6、stroke=currentColor、单 path 合并子路径），公共描边属性统一定义在 .alarm-popup-card svg 选择器；新增弹窗图标须沿用该风格。

同批落地的报警弹窗 UI 口径（AlarmPopupDialog.vue）：
- 「查看视频」入口位于右侧详细信息区（新增「视频」行，label 带视频 SVG），不再叠加在抓拍图片上；仍以 ownCameras 非空为显示条件。
- 尺寸口径：弹窗卡片 690px（相对最初 660px 整体扩宽 30px）；抓拍图片区 370×290；视频弹层 820px 不变。
- 顶栏警示图标用 #fadb14 黄；行内图标 14px、顶栏 20px、声音 18px、暂停 16px、无图占位 48px。
