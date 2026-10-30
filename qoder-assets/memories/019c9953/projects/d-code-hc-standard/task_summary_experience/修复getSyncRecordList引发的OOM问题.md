---
title: "修复getSyncRecordList引发的OOM问题"
usage_scenario:
    - "排查因SQL无分页/无过滤导致的内存溢出问题"
    - "优化高数据量查询SQL时添加时间范围限制"
    - "同类慢查询或OOM问题的根因分析与修复流程"
keywords:
    - "OOM"
    - "SQL优化"
    - "时间范围限制"
    - "getSyncRecordList"
---

## 任务描述
解决/sys-error.2026-07-19.log中OutOfMemoryError: GC overhead limit exceeded问题，根源为getSyncRecordList SQL无分页导致一次性加载14077条记录。

## 执行过程
```mermaid
graph TD
    A[日志分析] --> B[定位OOM堆栈至updTaskRecordStatus接口]
    B --> C[追溯调用链发现getSyncRecordList为瓶颈]
    C --> D[查询DB确认满足条件记录数=14077]
    D --> E[读取BaseRiskTaskRecordMapper.xml确认SQL缺失时间过滤]
    E --> F[添加AND r.check_time >= DATE_SUB(NOW(), INTERVAL 2 MONTH)]
    F --> G[验证修改后记录数降至3329条]
```

## 任务总结
通过日志→堆栈→代码→SQL→DB验证闭环，精准定位并修复OOM：在getSyncRecordList SQL中增加2个月时间范围限制，使待查记录从14077条降至3329条，降低内存压力76%，避免GC overhead触发。
