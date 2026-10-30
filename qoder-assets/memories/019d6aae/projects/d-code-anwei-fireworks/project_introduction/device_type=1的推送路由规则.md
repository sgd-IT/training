---
title: "device_type=1的推送路由规则"
usage_scenario: []
keywords:
    - "device_type=1"
    - "推送路由"
    - "ZBDY过滤"
    - "SXTXX"
source: "user"
---

项目数据推送链路对 device_type=1（摄像头）有明确路由规则：
- SBDY（联网设备）推送所有 device_type（含1）
- ZBDY（联网指标）和 ZBSS（实时数据）仅推送 device_type ∈ {3,4,8}，主动排除 device_type=1
- 摄像头相关数据通过独立的 SXTXX 链路推送
