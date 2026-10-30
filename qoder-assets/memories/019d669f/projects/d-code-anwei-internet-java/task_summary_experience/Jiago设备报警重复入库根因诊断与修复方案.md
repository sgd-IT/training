---
title: "Jiago设备报警重复入库根因诊断与修复方案"
usage_scenario:
    - "When diagnosing why a single device alarm appears as multiple records in the system"
    - "When designing an alarm de-duplication mechanism for time-sensitive device events"
keywords:
    - "Jiago"
    - "alarm duplicate"
    - "msgId"
    - "de-duplication"
    - "AluminumLeakageAlarmRecordServiceImpl"
source: "auto"
---

## Task description
- Core requirement: diagnose why a single Jiago device alarm results in two records in the system (e.g., 17:41:11 and 17:41:16)
- Task background: devices push alarms via /lyxl/push (generating new msgId each time) and the common alarm service creates records without checking for existing ones; the Redis Hash structure fixes audio matching but not event de-duplication

## Execution process
1. JiagoVideoLeakageController.java: confirmed that each /lyxl/push request generates a new msgId, even for the same continuous alarm event
2. AluminumLeakageAlarmRecordServiceImpl.java: confirmed that the public alarm service creates a new record for every valid msgId, with no logic to merge or deduplicate based on device + alarm time window
3. Verified the current implementation's behavior by analyzing code paths for msgId generation and record creation

## Related files
- d:\code\anwei-internet-java\monkey-monitor-api\src\main\java\com\monkey\general\modules\mon\JiagoVideoLeakageController.java
- d:\code\anwei-internet-java\monkey-monitor\src\main\java\com\monkey\general\modules\mon\service\impl\AluminumLeakageAlarmRecordServiceImpl.java

## Notes
- None

## Task overview
Successfully diagnosed the root cause of Jiago alarm duplicate入库: the backend lacks a de-duplication mechanism at the alarm record creation layer. Each device push generates a new msgId, and the common alarm service creates a new record for each, leading to multiple records for one physical alarm event. The Redis Hash structure resolves audio attachment, not event identity.
