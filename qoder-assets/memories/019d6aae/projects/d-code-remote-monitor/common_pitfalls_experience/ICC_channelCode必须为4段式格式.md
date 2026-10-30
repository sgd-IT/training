---
title: "ICC channelCode必须为4段式格式"
usage_scenario:
    - "调用ICC realMonitor或stream/realtime接口返回404或code=1001"
    - "拼接ICC设备通道码时不确定格式规范"
    - "排查ICC拉流失败问题时定位channelCode合法性"
keywords:
    - "ICC"
    - "channelCode"
    - "4段式"
    - "实时流"
source: "auto"
---

ICC实时流接口要求channelCode为4段式格式：deviceId$main$aux$channelNo（如1000067$1$0$0），而当前代码仅拼接为2段（deviceId$channelId），导致realMonitor接口404、stream/realtime接口返回code=1001失败。（来源：IccOpenApiClient）
