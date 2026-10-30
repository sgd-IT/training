---
title: "ICC视频流统一集成规范：4段式channelCode、鉴权、端口与协议"
usage_scenario:
    - "实现或调试实时视频拉流功能时查阅协议细节"
    - "排查FLV/HLS拉流失败时核对端口代理与混合内容"
    - "设计视频流保活或预热方案时核对ICC接口能力"
keywords:
    - "ICC"
    - "channelCode四段式"
    - "access_token"
    - "HLS"
    - "dhhls"
    - "端口7086"
    - "端口7886"
    - "flvUrl拼接"
    - "混合内容拦截"
    - "streamType主辅码流"
    - "AAC音轨"
source: "auto"
---

ICC视频流集成规范:
- channelCode格式为4段式: deviceId$main$aux$channelNo（如1000067$1$0$0）
- HTTP端口7086，HTTPS端口7096；HTTP-FLV 直连地址由 IccOpenApiClient.buildFlvStreamUrl 拼为 http://{ICC host}:7886/live/cameraid/{deviceId}%24{channelId}/substream/{quality}.flv?token={鉴权令牌}
- HLS 直连地址同构，位于 7086：http://{ICC host}:7086/live/cameraid/{deviceId}%24{channelId}/substream/{quality}.m3u8?token={鉴权令牌}；分片为相对路径 {quality}_seg_{seq}.ts，直播列表 TARGETDURATION=2、序号快速滚动（取旧序号会 404，须用当次 m3u8 内的分片名立即请求）
- 鉴权使用access_token，通过Authorization头传递
- 要求iConnection Center V5.0.12+、视频业务系统V1.1.2+、接口版本1.0.0
- 接口URL: /evo-apigw/admin/API/video/stream/realtime
- 请求方式: POST
- 数据格式: application/json
- 核心参数data为JSON字符串，含channelId（4段式，如"1000067$1$0$0"）、streamType（1主/2辅/3辅2）、type（hls/flv/ws_flv等）
- 后端通过ICC旧接口 /evo-apigw/admin/API/video/stream/realtime 获取HLS(m3u8)流地址，type字段硬编码为hls
- 音轨只存在于主码流：streamType=1 的 ES 为 HEVC(0x24)/H.264(0x1b) + AAC(0x0F, AAC-LC 8000Hz 单声道)，streamType=2 只有视频轨；同源 FLV 的 audio tag 数为 0。需要拾音必须点名取主码流，换协议无效。代价：主码流 2.2-3.3Mbps（分片 545-814KB/2s），子码流约 0.3Mbps（76KB/2s）。
- 后端 /eiot/video/camera/live/url 已支持可选 streamType 参数显式覆盖 video_camera.quality（不传则回落 camera.getQuality()），移动端按端传 '1' 取主码流；故意不改数据库 quality，因为该字段 PC 端 WSPlayer/VideoPlayer 共用，改成 1 会把 PC 多路分屏一起拖到主码流。
- 前端VideoPlayer.vue组件使用dhhls.min.js播放HLS流，通过全局Hls变量访问
- 部署限制（2026-11核实）：deploy/config/frontend/nginx.conf 只反代了 /wsplayer-proxy（9100 WebSocket）与 API，未代理 7886/7086，生产 https 页面直接拉 http://ICC:7886|7086 的 FLV/HLS 会被浏览器按混合内容拦截，且 ICC 为内网地址时外网不可达——这是 HLS/FLV 经常拉流失败的结构原因之一；WSPlayer 走同源 wss://域名/wsplayer-proxy 不受此限。小程序端 video 的 src 属媒体资源不受域名白名单校验，http 直连可播。
