---
title: "Jiago设备报警状态判断与过滤逻辑诊断任务"
usage_scenario:
    - "When diagnosing excessive Jiago device alarm generation"
    - "When verifying the correctness of status-based filtering logic in the alarm pipeline"
keywords:
    - "Jiago"
    - "alarm"
    - "status judgment"
    - "filtering logic"
    - "videoEventUp"
source: "auto"
---

## Task description
- Core requirement: diagnose why Jiago devices are generating excessive alarm records (e.g., 911 per day) and determine the role of the commented-out status judgment in videoEventUp
- Task background: The current code has a commented-out block in AluminumLeakageAlarmRecordServiceImpl.videoEventUp that checks for status="0"; the user suspects this is the root cause; the desktop jar file's behavior needs to be verified against the current code

## Execution process
1. Confirmed that the desktop jar's CastingWellInfoServiceImpl contains only getByNodeId, not resolveCastingWell, indicating it uses single-level resolution
2. Verified that the desktop jar's JiagoVideoLeakageController.receiveDeviceStatus places the Redis write inside the status="0" check but leaves eventUp outside, causing every status=0 upload to trigger an alarm regardless of casting state
3. Confirmed that the current code's /lyxl/push interface now wraps the entire processing block in the status="0" check, fixing the desktop jar's bug
4. Found that the old interfaces (/api/lyxl/eventUp and /mon/video/eventUp/api/alarmReceive/handle) still lack status filtering and directly call videoEventUp, leaving them vulnerable
5. Confirmed that the videoEventUp method's status check remains commented out in the current code, making it reliant on external filtering
6. Verified that the current code implements dual-layer filtering (resolveCastingWell + getCastingStatus) but lacks event-level deduplication

## Related files
- d:\code\anwei-internet-java\monkey-service\src\main\java\com\monkey\general\modules\tb\service\impl\CastingWellInfoServiceImpl.java
- d:\code\anwei-internet-java\monkey-monitor-api\src\main\java\com\monkey\general\modules\mon\JiagoVideoLeakageController.java
- d:\code\anwei-internet-java\monkey-monitor\src\main\java\com\monkey\general\modules\mon\service\impl\AluminumLeakageAlarmRecordServiceImpl.java

## Notes
- None

## Task overview
Successfully diagnosed the root cause of Jiago alarm over-generation: the desktop jar's /lyxl/push implementation incorrectly placed eventUp outside the status="0" check, while the current code fixes this for /lyxl/push but leaves the old interfaces unprotected. The videoEventUp status check remains commented out, making it dependent on external filtering. The current code also adds dual-layer filtering but lacks event-level deduplication.
