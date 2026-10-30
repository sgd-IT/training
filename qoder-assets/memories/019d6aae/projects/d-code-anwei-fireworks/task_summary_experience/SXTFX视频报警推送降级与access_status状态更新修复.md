---
title: "SXTFX视频报警推送降级与access_status状态更新修复"
usage_scenario:
    - "视频报警推送因设备编码不一致失败时的降级策略实现"
    - "报警类数据推送成功后统一更新access_status=2的状态维护"
    - "排查类似‘未找到视频设备’跳过日志的根因与修复路径"
keywords:
    - "SXTFX"
    - "access_status=2"
    - "设备编码降级"
source: "auto"
---

## 任务描述
修复视频报警数据（SXTFX）因设备编码不匹配导致推送跳过的问题，并统一恢复所有报警类推送成功后将access_status置为2的逻辑。

## 执行过程
```mermaid
graph TD
    A[日志分析：报警device_code与视频设备不匹配] --> B[确认根因：MAC地址vs视频设备code格式不一致]
    B --> C[设计降级方案：匹配失败时查企业全部device_type=1设备]
    C --> D[修改PushingGZDataService.saveInfoVideoAlarm方法]
    D --> E[恢复4处被注释的access_status=2更新逻辑]
    E --> F[编译验证通过]
```

## 任务总结
成功修复SXTFX推送失败问题：在saveInfoVideoAlarm中增加降级查询逻辑，当报警device_code无法匹配视频设备时自动回退至查询该企业全部视频设备；同时启用ZBBJ/RYCRJL/CLCRJL/SXTFX四类报警推送成功后的access_status=2状态更新，确保数据同步状态准确。
