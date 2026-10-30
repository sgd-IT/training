---
title: "ICC视频流接口保留策略-仅用旧接口+硬编码hls"
usage_scenario:
    - "新旧视频流接口共存时确定主用接口"
    - "规避新接口证书错误等稳定性问题"
    - "简化流类型参数管理，固定使用hls协议"
keywords:
    - "视频流接口"
    - "ICC"
    - "hls"
    - "接口兼容性"
    - "值班室"
source: "auto"
---

## 决策场景
视频流接口选型与维护

## 决策内容
仅保留旧版实时流接口 `/evo-apigw/admin/API/video/stream/realtime`，移除新接口 `/evo-apigw/admin/videoService/realMonitor`；流类型（type）字段硬编码为 `hls`

## 适用范围
ICC视频接入模块的接口演进与兼容性管理，避免因新接口不稳定影响线上服务
