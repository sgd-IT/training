---
title: "VideoPlayer 缓冲中指示与连接超时兜底修复"
usage_scenario:
    - "修改视频播放组件"
    - "排查视频黑屏问题"
    - "往播放器容器添加覆盖层"
keywords:
    - "VideoPlayer 缓冲指示"
    - "连接超时兜底"
    - "cleanupDhflvOverlays 白名单"
    - "黑屏反馈"
    - "H.265降级窗口"
source: "user"
---

用户反馈视频弹层"点击进去黑屏无加载反馈"。根因：VideoPlayer 只在 getLiveUrl 请求期有 loading 提示，流地址就绪后的拉流/缓冲阶段（含 H.265 降级窗口，最长约 10s）纯黑屏且无兜底。

修复（VideoPlayer.vue，全站共享组件，所有直播场景受益）：
- 新增 buffering 状态与"正在连接视频流..."覆盖层：activeUrl 就绪即显示；playing 事件隐藏；播放中 waiting 时延迟 300ms 显示（防瞬断闪烁）。
- 15s 超时兜底：仍未出画则 localError="视频流连接超时，请点击重试"，复用既有 error+retry 链路。
- createFlvPlayer/createHlsPlayer/createDhhlsPlayer 开头均 showBuffering(true)：H.265 降级与断线重连路径不丢指示；destroyPlayer 里 hideBuffering 清定时器。
- 关键坑：cleanupDhflvOverlays 的白名单必须同步加入任何新覆盖层类名（本次加入 .video-buffering 并补上 .play-overlay），否则 dhflv 清理时会把覆盖层 DOM 误删，造成 Vue 状态与 DOM 不一致。

验证：ESLint 0；vue-tsc 仅 1 条既有 TS6133（interceptH265 的 origFn，非本次引入）；SCSS 编译通过。
