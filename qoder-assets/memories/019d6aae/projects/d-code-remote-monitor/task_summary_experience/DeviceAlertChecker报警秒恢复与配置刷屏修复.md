---
title: "DeviceAlertChecker报警秒恢复与配置刷屏修复"
usage_scenario:
    - "When debugging protocol component alert logic defects"
    - "When extending or modifying the device alert checking mechanism"
    - "When onboarding new engineers to the alert rule engine"
keywords:
    - "DeviceAlertChecker"
    - "ConditionEvaluator"
    - "alarm秒恢复"
    - "配置刷屏"
    - "alert recovery logic"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the root cause of two symptoms — (1) alarms being instantly recovered ("alarm秒恢复") after triggering, and (2) a misconfigured alert rule flooding logs ("配置刷屏")
- Task background: both symptoms stemmed from the same bug in DeviceAlertChecker's recovery logic: when an alert condition key (e.g., data_subType) was absent from a message (like a heartbeat frame), the ConditionEvaluator returned false, and DeviceAlertChecker unconditionally called doRecover. This caused every non-event frame to trigger recovery, leading to "秒恢复" for triggered alarms and "刷屏" for misconfigured rules.

## Execution process
1. Codebase search and analysis: identified the core files involved — ConditionEvaluator.java (evaluates conditions), DeviceAlertChecker.java (orchestrates trigger/recover), DahuaEventParser.java (produces data_subType for UDP event frames), and GbtDataReporter.java (produces sub_type for TCP event frames)
2. Root cause confirmation: verified that ordinary frames (heartbeats/parameters) lack the condition fields (data_subType/sub_type), causing the false evaluation and subsequent unnecessary recover calls
3. Fix implementation:
   - ConditionEvaluator.java: added a new method hasAnyConditionField() to explicitly check if any condition key exists in the message data map
   - DeviceAlertChecker.java: modified processAlertConfig() to skip doRecover when no condition field is present, only performing recovery when the field exists but its value does not match
4. Compilation verification: successfully compiled the iot-rule-engine module with JDK 21

## Related files
- /module-eiot/module-eiot-core/iot-rule-engine/src/main/java/com/enjoyiot/eiot/ruleengine/devicealert/ConditionEvaluator.java
- /module-eiot/module-eiot-core/iot-rule-engine/src/main/java/com/enjoyiot/eiot/ruleengine/devicealert/DeviceAlertChecker.java

## Notes
- None

## Task overview
Successfully diagnosed and fixed the root cause of alarm秒恢复 and 配置刷屏: introduced a field-existence check before recovery, ensuring alarms persist until an explicit "解除帧" (e.g., subType=27) is received, and eliminating log spam from configuration mismatches.
