---
title: "前端UI元素禁用规范：禁摄像头状态标识与MSE播放器时间戳"
usage_scenario:
    - "新增或重构视频监控类页面时避免加被禁元素"
    - "审查视频组件是否存在已禁用的状态标识"
    - "判断某画面内浮层控件是否属于禁止项"
keywords:
    - "UI禁用"
    - "摄像头在线状态标识"
    - "视频时间戳"
    - "cell-rec"
    - "video-timestamp"
source: "auto"
---

前端 UI 中禁止显示摄像头在线状态标识（.cell-rec、.cell-rec-dot、.cell-status、.cell-status-dot）和视频时间戳（.video-timestamp），该约束适用于 VideoPlayer(dhflv/dhhls) 体系的视频监控页面。

例外与新发现（2026-10-16 报警弹窗改造实测）：
- WSPlayer 播放时画面自带的通道名 OSD（底部）与时间戳（右上）由大华闭源 SDK 绘制，前端样式无法移除，不属于本条禁止范围，不要为此去改 WSPlayer 侧叠层或反复调 CSS。
- 在 WSPlayer 画面上叠加"摄像头切换标签"这类功能性控件是允许的既定形态（AlarmVideoDialog.vue 的 .camera-tags），但必须注意其 z-index 需高于 WSPlayer 画面窗口（详见坑位记忆"WSPlayer 画面层内联 z-index 100 会盖住叠加的 HTML 浮层"）。
