---
title: "ICC接口诊断日志增强（含channelSeq确认）"
usage_scenario: []
keywords:
    - "ICC日志"
    - "channelSeq"
    - "辅码流"
    - "通道匹配"
---

## 任务描述
为诊断辅码流无法实时回放问题，在ICC接口中添加结构化日志，重点确认channelSeq基序（0-based/1-based）及辅码流请求响应细节。

## 执行过程
```mermaid
graph TD
    A[问题:辅码流实时回放失败] --> B[分析日志缺失点]
    B --> C[定位ICC三大接口方法]
    C --> D[在getDeviceDetail中遍历打印所有通道的channelCode/channelSeq/channelName]
    D --> E[在getRealtimeStream中补全streamType请求参数与响应code/desc/URL]
    E --> F[统一添加[ICC]前缀并修复getPlaybackStream日志]
```

## 任务总结
成功添加诊断日志：① `getDeviceDetail`中遍历打印通道信息，可直接确认`channelSeq`值（用于判断通道匹配是否错位）；② `getRealtimeStream`中强化`streamType`和响应详情日志；③ 所有日志统一`[ICC]`前缀，支持`grep "[ICC]"`快速过滤。
