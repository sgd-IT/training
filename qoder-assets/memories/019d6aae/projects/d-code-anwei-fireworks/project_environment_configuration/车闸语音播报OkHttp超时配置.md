---
title: "车闸语音播报OkHttp超时配置"
usage_scenario:
    - "配置语音播报服务网络超时时参考设备实际响应时间"
    - "排查语音播报延迟或超时问题时检查OkHttp超时设置"
    - "新接入类似音柱设备时确定合理超时阈值"
keywords:
    - "车闸语音播报"
    - "OkHttp超时"
    - "8秒超时"
source: "auto"
---

车闸语音播报设备实际响应耗时为6秒，OkHttp客户端超时配置需设为8秒（connectTimeout、readTimeout、writeTimeout均为8秒），以确保正常响应不超时，同时避免设备异常时长时间阻塞。
