---
title: "ICC接口响应日志规范"
usage_scenario: []
keywords:
    - "ICC日志"
    - "响应日志"
    - "[ICC]前缀"
    - "channelSeq"
---

ICC 所有对外调用接口（getDeviceDetail、getDevicePage、getRealtimeStream）必须记录结构化响应日志，包含：① 响应状态字段（code/success/errMsg 或 code/desc）；② 关键业务数据（如 channelSeq/channelCode/channelName）；③ 请求上下文（channelId、streamType）；④ URL 处理过程（原始URL、token拼接后URL）；所有日志统一添加 [ICC] 前缀以便过滤。
