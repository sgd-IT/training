---
title: "设备与告警表tenant_id为NOT NULL DEFAULT 0，Java侧永不为null"
usage_scenario:
    - "判断租户字段判空回退是否冗余"
    - "排查设备或告警记录对租户不可见问题"
    - "编写涉及 tenant_id 的推送或查询条件"
keywords:
    - "NOT NULL DEFAULT 0"
    - "eiot_device_info"
    - "eiot_alert_record"
    - "tenant_id为0"
    - "null回退死代码"
source: "auto"
---

eiot_device_info.tenant_id 与 eiot_alert_record.tenant_id 在 _sql/mysql/system.sql 中均定义为 `bigint NOT NULL DEFAULT 0`，即数据库层面不可能存 NULL，未指定租户时取值为 0（AEP 自动注册设备即刻意保持 tenant_id=0）。因此凡从这两张表读出的实体（DeviceInfo.getTenantId()、AlertRecordDO.getTenantId()）在 Java 侧最小是 0L 而非 null，围绕它们编写的 `if (x == null)` 回退分支属于永不触发的死代码。副作用：tenant_id=0 的记录对任何真实租户都不可见（租户插件按 tenant_id 过滤），但会被当作非 null 值传入告警实时推送门面 AlarmRealtimeNotifyService.notifyAlarmSaved，从而多走一次"租户 0"的空推送与注定失败的 tenantName 查询（有 try-catch 兜底，行为无害）；若需彻底跳过 0，应在门面把判断改为 alarmTenantId != null && alarmTenantId > 0，但这会改变所有链路的通用行为。
