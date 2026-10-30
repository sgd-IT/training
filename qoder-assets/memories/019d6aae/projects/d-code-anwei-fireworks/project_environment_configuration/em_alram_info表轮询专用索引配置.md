---
title: "em_alram_info表轮询专用索引配置"
usage_scenario:
    - "优化定时轮询数据库查询性能时添加索引"
    - "新环境部署时初始化数据库索引配置"
    - "排查轮询慢查询问题时检查索引覆盖"
keywords:
    - "em_alram_info"
    - "索引优化"
    - "alarm_status"
    - "warning_date"
source: "auto"
---

报警信息表 `em_alram_info` 已有索引 `idx_alarmtype_date_status(alarm_type, warning_date, processing_status)`，但轮询查询使用 `alarm_status` 和 `warning_date` 字段，需新增专用索引 `idx_alarm_status_date(alarm_status, warning_date)` 以保障定时轮询性能。
