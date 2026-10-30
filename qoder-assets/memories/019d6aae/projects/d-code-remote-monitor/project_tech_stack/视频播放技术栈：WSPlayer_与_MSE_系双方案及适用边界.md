---
title: "视频播放技术栈：WSPlayer 与 MSE 系双方案及适用边界"
usage_scenario:
    - "新增或改造视频播放入口时选播放器方案"
    - "排查FLV/HLS拉流失败或起播慢问题"
    - "评估小程序能否播某协议或需否类目权限"
keywords:
    - "WSPlayer"
    - "WsPlayerWrapper"
    - "dhflv"
    - "dhhls"
    - "bufferAddCodecError"
    - "H.265软解"
    - "channelCode四段式"
    - "wsplayer-proxy"
    - "混合内容拦截"
    - "小程序live-player类目"
    - "小程序video组件格式"
source: "auto"
---

项目视频播放能力基于大华设备生态，两套方案并存：
1. WSPlayer 体系（WsPlayerWrapper.vue）：WebSocket + RTSP + WebAssembly 软解，支持 H.264/H.265；入参 camera 需 deviceId/channelId/quality 拼 4 段式 channelCode。已用于 video-surveillance、video-manage、remote-console/VideoPanel、报警弹窗视频弹层 AlarmVideoDialog.vue（共 4 处）。https 页面经 nginx 同源 /wsplayer-proxy 转发，无混合内容问题。协议本质是 RTSP over WebSocket：ICC 返回的 rtsp:// 地址端口与 WSPlayer 连接的 ws:// 端口同为 9100（流媒体在 9100 上同时承载 RTSP 信令与 WS 帧，把 RTSP 装在 WebSocket 帧里传），浏览器无原生 RTSP 能力故必须靠这层承载 + WASM 解码，不经 MediaSource。Wrapper 配置 useH265MSE: true，H.265 优先尝试 MSE 硬解省 CPU，解码库 static 资源作回退；token 一次性且 30s 时效，须在 SDK 初始化完成后回调取流、每次播放重新取，不可缓存。
2. MSE 系播放器（VideoPlayer.vue 内 dhflv.min.js / dhhls.min.js / hls.js）：HTTP-FLV(7886) 优先、HLS 兜底。坑：值班室这几路摄像头用 dhflv/dhhls 会报 hls.js bufferAddCodecError（MSE 不支持该流编码），并非所有流都能靠 MSE 播；遇到解码失败应切 WSPlayer 而非继续调 HLS 参数。
稳定性实测对比（2026-11 核对）：HLS/FLV 经常拉流失败而 WSPlayer(RTSP over WS) 稳定约 90%，结构性根因有三——①MSE 只支持浏览器解码器能解的编码，H.265 主码流原理上播不出，三层降级到 dhhls 仍是 MSE 无法解决；②FLV/HLS 地址是 ICC 直连（http://host:7886/live/...、7086），deploy/config/frontend/nginx.conf 只代理了 /wsplayer-proxy 而无 7886/7086，https 页面下 http 流被浏览器按混合内容拦截；③HLS 冷启动建流 15-30s 且 token 一次性 30s 时效对多切片续拉不友好。
ws_flv 替代方案结论（否决）：ICC type 参数虽支持 ws_flv，但它只换传输层为 WebSocket，封装仍是 FLV、解码仍走 MSE，H.265 照样 bufferAddCodecError；且项目历史上已因代理环境下 WS 不稳主动禁用过 dhflv 的 http→ws 自动转换，全项目 grep ws_flv 零匹配。要付"加 WS 通道 + nginx 反代"的成本就直接复用现成 WSPlayer。
现存 MSE 系落点：PC 告警工单 alarm-order/index.vue「现场视频」（纯 HLS，只取 urlRes.url 未用 flvUrl）、移动端 uni-app video-monitor。
移动端播放能力准确口径（2026 拾音任务实测修正，替换旧表述"原生 video 只认 HLS/MP4、只能 HLS"）：小程序没有 MSE/WASM/DOM 环境，WSPlayer 与 hls.js/dhflvjs 全不可移植。播放能力由两个组件分工、互不通用——video 组件无类目限制，支持 mp4/mov/m4v/3gp/avi/m3u8(HLS)/webm，编码支持 H.264/HEVC/MPEG-4/VP9，但不支持 flv/rtmp/rtsp；live-player 组件仅支持 flv/rtmp、不支持 m3u8，且为企业主体+特定类目白名单门槛（消防/物业/安防类大概率不在开放列表，须先在小程序后台确认能否自助开通再谈改造）。RTSP 双组件都不支持，必须服务端转协议。iOS 原生播 HLS 对 HEVC 支持良好，故 PC 的 H.265+MSE 问题在小程序端不存在。关键结论：小程序"有没有声音"取决于码流选择而非协议切换——音轨只在 ICC 主码流里，子码流是纯视频，同源 FLV 的 audio tag 数为 0，换 live-player 也解决不了拾音（详见记忆"ICC 码流音频差异"）。起播慢的矛盾是 ICC 侧流媒体冷启动建流，优化方向是上游保活预热而非换播放器；pages/video-monitor/index.vue 的 PLAY_TIMEOUT 已由 10s 放宽到 40s（原值小于 15-30s 建流耗时，冷启动必被判失败）。
选型原则：在新位置放视频时先确认目标流编码兼容性——不确定就用 WSPlayer（软解覆盖面更广）；PC 端剩余 HLS 落点迁移可照抄 AlarmVideoDialog 改法（前端零后端改动）。
重要差异：WSPlayer 播放画面由闭源 SDK 自绘，底部带通道名 OSD、右上带时间戳，前端 CSS 无法移除；在其画面上叠加 HTML 控件须注意层级（见"WSPlayer 画面层内联 z-index 100 会盖住叠加的 HTML 浮层"坑位记忆）。
