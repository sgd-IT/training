---
title: "广西推送 alarm_type 语义映射修复"
usage_scenario:
    - "对接省级监管平台（如广西、贵州）时报警类型编码不一致的修复"
    - "排查报警数据在监管平台显示类型错误（如遮挡显示为温度）的根本原因"
    - "自研AI算法报警类型与外部平台规范对齐的通用改造方案"
keywords:
    - "alarm_type"
    - "广西推送"
    - "类型映射"
source: "auto"
---

## 任务描述
修复广西数据推送中 alarm_type=6 语义不一致问题：本地系统将摄像头遮挡标记为6，但广西平台规范中6代表温度报警，5才代表摄像头遮挡偏移。

## 执行过程
```mermaid
graph TD
    A[问题:广西未正确识别摄像头遮挡报警] --> B[溯源alarm_type=6来源]
    B --> C[定位到GetCameraAlarmInfoController中occluded→alarm_type=6]
    C --> D[确认SetGXUploadInfo.java未做类型映射，直接透传]
    D --> E[新增mapToGxAlarmType静态方法，实现6→5映射]
    E --> F[在setGXAlarmInfoData中调用映射方法，并增加null安全判断]
```

## 任务总结
成功修复广西报警类型语义错配：在 SetGXUploadInfo.java 中新增 mapToGxAlarmType 方法，将本地 alarm_type=6（摄像头遮挡）统一映射为广西规范值 5（摄像头遮挡偏移），并在调用处增加 null 判断，确保推送数据符合广西平台接口规范。
