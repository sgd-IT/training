---
title: "Jiago设备报警过滤逻辑诊断：设备映射与铸造状态双层过滤机制"
usage_scenario:
    - "When diagnosing why Jiago device alarms fail to appear in the system despite physical events"
    - "When verifying the correctness of the casting status filtering logic against PLC signal sources"
    - "When debugging Jiago device mapping resolution failure for a specific device SN"
keywords:
    - "Jiago"
    - "alarm filtering"
    - "resolveCastingWell"
    - "getCastingStatus"
    - "device mapping"
    - "casting status"
source: "auto"
---

## Task description
- Core requirement: diagnose whether Jiago devices not added to the device table would bypass alarm filtering and cause false alarms during non-casting periods
- Task background: user suspected that missing device mapping would skip casting status filtering; actual code implements two-layer hard rejection: device mapping resolution (resolveCastingWell) and casting status evaluation (getCastingStatus); both must pass for an alarm to be recorded

## Execution process
1. JiagoVideoLeakageController.java: confirmed resolveCastingWell is called first; if it returns null (no mapping), interface returns 500 immediately — no record created, no audio saved, no broadcast
2. AluminumLeakageAlarmRecordServiceImpl.videoEventUp: only entered after successful mapping; getCastingStatus then checks PLC signals (ax0047, ax0024/ax0048) and PRE_SIGNAL_KEY; only signal==1 triggers recording
3. Verified signal source health: ax0083 (pre-start) and ax0047 (casting) are real-time and zero-delay on cloud DB
4. Confirmed em_aluminum_device_info data_status=0 disables mapping — same as missing entry

## Related files
- d:\code\anwei-internet-java\monkey-monitor-api\src\main\java\com\monkey\general\modules\mon\JiagoVideoLeakageController.java
- d:\code\anwei-internet-java\monkey-monitor\src\main\java\com\monkey\general\modules\mon\service\impl\AluminumLeakageAlarmRecordServiceImpl.java
- d:\code\anwei-internet-java\monkey-service\src\main\java\com\monkey\general\modules\tb\service\impl\CastingWellInfoServiceImpl.java

## Notes
- None

## Task overview
Successfully diagnosed the full Jiago alarm filtering chain: devices not in em_aluminum_device_info (or with data_status=0) are rejected at resolveCastingWell with HTTP 500 — no alarm record or audio is ever created. Even if mapped, non-casting status (signal==0) causes silent ignore in videoEventUp. The "non-casting alarm" phenomenon must originate from signal chain misconfiguration, not device table omission.
