---
title: "Alarm model must separate alarm type and severity level"
usage_scenario:
    - "When designing or refactoring an alarm model for IoT platforms"
    - "When troubleshooting inconsistent alarm display across different components"
keywords:
    - "alarm model"
    - "alarm type"
    - "severity level"
    - "data model"
source: "auto"
---

## Task description
- Core requirement: identify and document the root cause of inconsistent alarm display across different components
- Task background: different components interpret the same 'level' field differently (e.g., firewatch popup vs alarm record page), causing confusion and incorrect alert handling

## Execution process
1. Code analysis revealed that AlarmRecordService.java maps various alarm types and severity levels into a single 'level' field during database persistence
2. Frontend components (duty-screen/index.vue, alarm-record/index.vue) interpret the 'level' field using different mapping logic, leading to inconsistent display
3. The root cause was confirmed by tracing the data flow from device reporting through parsing to frontend consumption

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AlarmRecordService.java
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\src\views\eiot\firewatch\duty-screen\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\src\views\eiot\firewatch\alarm-record\index.vue

## Notes
- None

## Task overview
Successfully identified and documented the root cause of inconsistent alarm display: the alarm model conflates alarm type and severity level into a single 'level' field, which is interpreted differently by different components. This architectural flaw must be addressed by separating these two dimensions in the data model.
