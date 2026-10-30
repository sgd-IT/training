---
title: "FLV播放器协议降级决策-禁用ws/wss自动转换"
usage_scenario:
    - "FLV播放在WebSocket连接频繁断开时需切换回HTTP-FLV"
    - "前端视频组件因代理限制无法建立ws连接而需绕过协议升级逻辑"
    - "排查FLV卡顿问题时确认是否由ws层引入额外延迟"
keywords:
    - "FLV"
    - "WebSocket"
    - "HTTP-FLV"
    - "协议降级"
source: "auto"
---

## 决策场景
FLV视频播放器协议选择策略调整

## 决策内容
放弃WebSocket FLV（ws/wss）方案，回归使用原始HTTP-FLV地址直接播放，移除协议自动转换逻辑

## 适用范围
基于dhflv.js的前端视频播放组件中，当代理或网络环境对WebSocket支持不稳定时，需降级回HTTP-FLV

## Notes（适用边界补充）
本决策只针对"dhflv.js播放器内部的http→ws协议自动转换层"被禁用这一件事，不等于项目整体拒绝ws拉流。当业务确实需要WebSocket类型拉流时（如告警弹窗查看视频），应改用项目现成的WSPlayer体系（WsPlayerWrapper.vue：经WebSocket连流媒体、WebAssembly软解、支持H.264/H.265），而不是去重新打开dhflv的ws转换。判据：若诉求是"换一套能跑ws的播放器"则走WSPlayer；若只是"dhflv别再自作主张把http-flv转成ws"才适用本条禁用逻辑。

关于 ws_flv 能否替代 HLS/FLV 解决拉流失败（2026-11 核对结论：能走但不值得走）：
- ICC 的 stream/realtime 接口 type 参数虽支持 ws_flv，后端 scheme 为透传，技术上可切；但 ws_flv 只替换传输层，封装仍是 FLV、解码仍走 MediaSource，浏览器解码器不支持的 H.265 照样报 bufferAddCodecError，因此它不能作为 H.265 主码流拉流失败的修复方案。加一条 wss 反代仅能顺带绕过混合内容拦截与端口不可达这两个次要因素。
- 现状证据：VideoPlayer.vue 传的是 type: 'flv' + http 地址，全项目 grep ws_flv/wsflv/ws-flv 零匹配，无任何 ws_flv 实现残留。
- 若未来确需尝试（仅适用于 H.264 流 + 外网受限于混合内容/端口不可达的组合场景），最小改动为：后端 IccOpenApiClient.getRealtimeStreamUrlLegacy 的 type 传 ws_flv、nginx 增加转发至流媒体 7886/7887 的 wss location（带 WS 升级头）、前端将该地址交给 dhflv。同等工程成本下应优先复用已稳定约 90% 的 WSPlayer，而非新造 ws_flv 通道。
