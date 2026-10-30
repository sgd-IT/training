---
title: "video-surveillance 视频墙布局切换的300ms防抖规范"
usage_scenario:
    - "改造 video-surveillance 视频墙布局切换逻辑"
    - "排查视频墙重复拉流请求问题"
keywords:
    - "防抖"
    - "layoutMode"
    - "video-surveillance"
    - "视频墙布局切换"
    - "重复拉流"
source: "auto"
---

仅限前端视频监控页 src/views/eiot/firewatch/video-surveillance/index.vue：切换视频墙布局模式（single/2×2/3×3）时，对 layoutMode 监听器应用 lodash-es debounce 300ms，避免快速连续切换导致重复拉取视频流请求。
不适用：报警弹窗视频弹层（AlarmVideoDialog）等单路播放场景——那里的摄像头切换走 watch(selectedCameraId) 直接换流 + 过期响应丢弃，不需要也不应加防抖。
