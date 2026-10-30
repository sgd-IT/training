---
title: "修复updTaskRecordStatus接口引发的OOM问题"
usage_scenario:
    - "排查因接口隐式全量操作导致的JVM内存溢出问题"
    - "优化高并发场景下批量数据同步的内存占用"
    - "改造存在‘更新即同步’耦合逻辑的业务方法"
keywords:
    - "OOM"
    - "分批处理"
    - "隐式同步"
    - "SQL LIMIT"
---

## 任务描述
解决系统因`updTaskRecordStatus`接口隐式全量同步导致的`OutOfMemoryError: GC overhead limit exceeded`问题，通过解耦更新与同步逻辑、SQL限流、分批处理、补充定时任务四步实现根治。

## 执行过程
```mermaid
graph TD
    A[日志分析定位OOM] --> B[发现updateBaseRiskTaskRecord隐式加载3000+条记录]
    B --> C[移除隐式同步逻辑，使update仅做状态更新]
    C --> D[在getSyncRecordList SQL添加LIMIT 200]
    D --> E[重构syncRiskTaskRecord为while循环+内层50条分批]
    E --> F[在SyncShuangKongTask新增定时同步方法，每5分钟执行]
```

## 任务总结
成功消除OOM风险：1）`updateBaseRiskTaskRecord`剥离同步职责；2）SQL查询限制为200条；3）`syncRiskTaskRecord`支持分批处理（50条/批）；4）新增定时任务保障同步兜底；5）同步内存峰值从3000+条降至200条以内，彻底规避并发OOM。
