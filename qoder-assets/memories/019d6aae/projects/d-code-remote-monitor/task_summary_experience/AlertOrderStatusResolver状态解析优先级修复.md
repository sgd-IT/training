---
title: "AlertOrderStatusResolver状态解析优先级修复"
usage_scenario:
    - "诊断告警中心状态显示异常"
    - "重构 AlertOrderStatusResolver 的状态解析优先级"
    - "同步 Java 解析逻辑与 SQL CASE 语句"
keywords:
    - "AlertOrderStatusResolver"
    - "handle_status"
    - "details"
    - "status priority"
    - "alarm center"
source: "auto"
---

## Task description
- Core requirement: fix the status display inconsistency in the alarm center where handle_status=2 (处置中) was incorrectly displayed as "已关闭"
- Task background: AlertOrderStatusResolver.resolveOrderStatus() had flawed priority order — it parsed details text first, then checked handle_status; this caused "关闭工单" in notification templates to override the authoritative handle_status field

## Execution process
1. Diagnosed the root cause by tracing through AlertOrderStatusResolver.java and comparing Java resolver logic with SQL ORDER_STATUS_CASE_SQL
2. Identified that details field contains both notification template (【消防告警通知】...) and Web处置 records ([...]-关闭工单), causing false positive matches
3. Refactored AlertOrderStatusResolver.resolveOrderStatus() to prioritize handle_status (2→handling, 3→handled) before falling back to details parsing
4. Synchronized SQL ORDER_STATUS_CASE_SQL to match the new priority order
5. Verified the fix resolves the user's issue: handle_status=2 now correctly displays as "处置中"

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\firewatch\AlertOrderStatusResolver.java

## Notes
- None

## Task overview
Successfully fixed the alarm center status display inconsistency by reordering AlertOrderStatusResolver's priority logic to make handle_status authoritative; the fix ensures handle_status=2 correctly displays as "处置中" instead of "已关闭".

## Notes
- 取值口径补充（2026-10 alarm-assist 接口会话实测）：本条的 2→处置中、3→已处置映射与 DDL 注释一致（完整集为 0=待处置/NULL、1=稍后处置、2=处置中、3=已处置）。据此派生的**未闭环（报警中）判据 = handle_status IS NULL OR < 3**。凡涉及 handle_status 取值或未闭环过滤，以记忆《AlertOrderStatusResolver与handle_status取值口径》为准，不要从本条推断。
