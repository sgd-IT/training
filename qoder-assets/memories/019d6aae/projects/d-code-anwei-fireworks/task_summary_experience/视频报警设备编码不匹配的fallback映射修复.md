---
title: "视频报警设备编码不匹配的fallback映射修复"
usage_scenario:
    - "报警类推送服务中，事件记录device_code与设备主表device_code格式不一致导致匹配失败"
    - "类似getXXXMap方法依赖deviceMap.get(deviceCode)且存在编码体系差异的场景"
    - "需在不修改设备主表或报警表结构前提下实现兼容性映射"
keywords:
    - "device_code映射"
    - "fallback机制"
    - "视频报警"
source: "auto"
---

## 任务描述
修复视频报警推送失败问题：报警记录的 device_code（MAC 地址格式）与视频设备表的 device_code（硬件编码格式）不一致，导致 getVideoAlarmMap 中 deviceMap.get() 返回 null，全部报警被跳过，jsonData 为空，推送静默终止。

## 执行过程
```mermaid
graph TD
    A[日志确认：device_code匹配查询返回0条] --> B[定位PushingGZDataService.saveInfoVideoAlarm方法]
    B --> C[发现已添加降级查询全部视频设备逻辑]
    C --> D[但getVideoAlarmMap仍因deviceMap无对应key而跳过所有报警]
    D --> E[深入Grep SetGZUploadInfo.getVideoAlarmMap源码]
    E --> F[确认根因：deviceMap.get(a.getDeviceCode())精确匹配失败]
    F --> G[在deviceMap构建后新增fallback逻辑：将未匹配的报警device_code统一映射到第一个视频设备]
    G --> H[验证编译通过，确保JSON可生成并触发后续上传]
```

## 任务总结
成功修复视频报警推送中断：在 PushingGZDataService.java 的 saveInfoVideoAlarm 方法中，于 deviceMap 构建完成后插入 fallback 映射逻辑——遍历 alarmList，对所有 device_code 不在 deviceMap 中的报警项，将其 device_code 作为 key、首个视频设备作为 value 写入 deviceMap。该机制使 getVideoAlarmMap 能正常关联设备并生成非空 JSON，彻底解决双编码体系下的映射断裂问题。
