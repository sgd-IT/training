---
title: "报警表sync_id字段不一致性根因分析与修复方案"
usage_scenario:
    - "排查报警表数据一致性问题时分析sync_id缺失原因"
    - "设计新报警模块时参考统一创建规范"
    - "重构现有报警处理逻辑时确保sync_id一致性"
keywords:
    - "sync_id"
    - "报警表"
    - "em_alram_info"
    - "统一创建规范"
    - "根因分析"
source: "auto"
---

## Task description
- Core requirement: analyze why some alarm records created through the /getCameraAlarmInfo endpoint have sync_id while others don't
- Task background: the em_alram_info table receives data from over 10 different code paths; sync_id is inconsistently set across these paths, causing confusion in downstream logic like voice broadcasting

## Execution process
1. Analyzed GetCameraAlarmInfoController.processAlarmInfo() → handleAlarmType() → alramInfoService.save(): confirmed this path never sets sync_id
2. Grep'd all sync_id assignments across the codebase: identified 10+ paths that do set it (QingTianService L738/L874, DaHuaService, DataJXUtil, RadarService, etc.) and 9+ paths that don't (GetAlgorithmAlarmController, AlramInfoController.save, ChengLianService, Jt808ServerHandler, etc.)
3. Concluded the root cause: lack of unified alarm record creation规范; each module implements independently
4. Proposed solution: extract a unified AlramInfo factory method to ensure consistent sync_id assignment

## Related files
- /monkey-monitor-api/src/main/java/com/monkey/general/python/GetCameraAlarmInfoController.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/third/service/QingTianService.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/third/service/DaHuaService.java
- /monkey-monitor/src/main/java/com/monkey/general/config/mqtt/util/DataJXUtil.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/third/service/RadarService.java

## Notes
- None

## Task overview
Successfully completed the root cause analysis of sync_id inconsistency: confirmed that /getCameraAlarmInfo never sets sync_id while other paths like QingTianService do; mapped all 10+ setting and 9+ non-setting paths; concluded the issue stems from missing unified creation规范; proposed a factory method solution.
