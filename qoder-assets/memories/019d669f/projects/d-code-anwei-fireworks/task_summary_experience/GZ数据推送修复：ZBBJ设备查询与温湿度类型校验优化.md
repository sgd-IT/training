---
title: "GZ数据推送修复：ZBBJ设备查询与温湿度类型校验优化"
usage_scenario:
    - "当修复贵州（GZ）数据推送中ZBBJ设备查询逻辑不一致问题时"
    - "当收紧GZ数据温湿度类型校验规则以符合规范附录表11时"
keywords:
    - "GZ推送"
    - "ZBBJ"
    - "target_type"
    - "validTargetCodes"
    - "access_status"
source: "auto"
---

## Task description
- Core requirement: fix GZ (Guizhou) data push logic inconsistencies and tighten validation rules
- Task background: `PushingGZDataService.java` had incomplete ZBBJ/BJCZ device filtering; `validTargetCodes` construction differed from ZBDY; `GZDataValidator.java` accepted overly broad `target_type` values — all violating spec alignment; goal was to unify behavior and enforce strict validation.

## Execution process
1. `PushingGZDataService.java`: added `access_status = 0` filter to ZBBJ/BJCZ device queries, aligning with ZBDY/SBDY behavior
2. `PushingGZDataService.java`: refactored `validTargetCodes` query construction to match ZBDY exactly, including test-mode `LIMIT` clause
3. `SetGZUploadInfo.java`: updated ZBBJ alarm map field mapping logic to reflect latest alarm type definitions
4. `GZDataValidator.java`: restricted `target_type` validation to accept only `"01"` and `"02"` per specification Appendix Table 11
5. Verified changes via `git diff`, staged only the 3 Java files, and committed with a semantic message

## Related files
- monkey-monitor/src/main/java/com/monkey/general/platform/push/gz/PushingGZDataService.java
- monkey-monitor/src/main/java/com/monkey/general/platform/push/gz/SetGZUploadInfo.java
- monkey-monitor/src/main/java/com/monkey/general/util/gz/util/GZDataValidator.java

## Notes
- `application.yml` profile switch (prod→dev) was correctly identified as local-only and excluded from commit
- IDE config (`.idea/jarRepositories.xml`) and build artifacts (`build_output.txt`) were also correctly ignored

## Task overview
Successfully fixed GZ data push logic: unified ZBBJ/BJCZ device filtering and `validTargetCodes` generation with ZBDY; updated ZBBJ alarm mapping; enforced strict `target_type` validation (`"01"/"02"`); all changes committed cleanly in one atomic commit (`741a5ca1`).
