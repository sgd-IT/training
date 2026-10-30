---
title: "access_status字段三值语义规范"
usage_scenario: []
keywords:
    - "access_status"
    - "状态语义"
    - "增量上传"
    - "贵州平台"
---

项目中`access_status`字段采用三值语义：
- `0`：未接入
- `1`：已接入、待推送到贵州平台
- `2`：已成功推送到贵州平台
该状态用于人员出入(RYCRJL)、车辆出入(CLCRJL)、监测指标报警(ZBBJ)、视频报警(SXTFX)四类数据的增量上传控制。
