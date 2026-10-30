---
title: "MySQL JSON列写入格式错误诊断与修复"
usage_scenario:
    - "When diagnosing MySQL JSON column insertion failures"
    - "When fixing plain-string-to-JSON-column mismatches in Java services"
keywords:
    - "MySQL JSON"
    - "extra_info"
    - "OverStorageDetectionService"
    - "AlarmRecordServiceImpl"
    - "JsonUtils"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the "Invalid JSON text" error when inserting into `fireworks_alarm_record.extra_info`
- Task background: `extra_info` is a JSON-type column in MySQL; the `OverStorageDetectionService.checkAndAlert()` method writes plain Chinese strings like "当前库存: 118kg..." directly to it, violating JSON format; the same issue exists in `AlarmRecordServiceImpl.insert()` via `bo.getRemark()`

## Execution process
1. Read `pasted-text.txt`: confirmed error is format-related, not overstock
2. Read `OverStorageDetectionService.java`: located line 159-162 where plain string is assigned to `alarm.setExtraInfo()`
3. Grep `setExtraInfo`: found three callers — `AlarmReportServiceImpl`, `OverStorageDetectionService`, `AlarmRecordServiceImpl`
4. Read `AlarmRecordServiceImpl.java`: confirmed line 287 uses `bo.getRemark()` as plain string for `extraInfo`
5. Proposed fix: use `JsonUtils.toJsonString(Map.of(...))` in both `OverStorageDetectionService` and `AlarmRecordServiceImpl` to ensure valid JSON output

## Related files
- c:\Users\ADMINI~1\AppData\Local\Temp\aicoding-text-field\b98469d8-6f10-4c6c-b95e-68e1e90512bd\text-field-content-41f69676-2d63-486c-a018-d00fecff04da.txt
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\OverStorageDetectionService.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\AlarmRecordServiceImpl.java

## Notes
- None

## Task overview
Successfully diagnosed the root cause of the MySQL JSON column violation: `extra_info` is a JSON-type column but code writes plain strings. Identified all three call sites (`OverStorageDetectionService`, `AlarmRecordServiceImpl`, `AlarmReportServiceImpl`) and proposed using `JsonUtils.toJsonString()` to produce valid JSON. The fix ensures data integrity and prevents future insert failures.
