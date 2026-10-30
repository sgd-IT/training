---
title: "Risk unit start/stop time alignment diagnosis and solution design"
usage_scenario:
    - "When diagnosing and resolving time-alignment issues for risk unit start/stop operations"
    - "When designing a solution for precise, on-demand state switching in a scheduled system"
keywords:
    - "risk unit"
    - "start/stop time"
    - "time alignment"
    - "diagnosis"
    - "solution design"
source: "auto"
---

## Task description
- Core requirement: diagnose why the customer's risk unit start/stop times are delayed by up to 24 hours and design a solution for precise, on-time execution.
- Task background: The customer requires that when they set a 'start time' or 'stop time' for a risk unit in the PC platform, the system must execute the status change and associated task dispatch/closure at exactly that moment. Current behavior shows delays due to two independent causes: immediate state change on record creation and low-frequency scheduled correction.

## Execution process
1. Verified the current MCP connection status and confirmed the active service name is 'tgr-gz-vue', landing on the production instance 'anwei1-0001'.
2. Confirmed the scheduling configuration: job_id=104 (TaskList.addRiskTaskRecord) runs daily at 00:10; job_id=105 (TaskList.updateRiskTaskRecordStatus) runs every 10 minutes; DataSyncTask.autoSyncUnitStatus is triggered only at 23:30 and 6:00.
3. Traced the code path for immediate state change: BaseRiskUnitLogServiceImpl.insertBaseRiskUnitLog unconditionally sets the unit status to '1' (stopped) upon record creation, regardless of the configured stopTime.
4. Traced the code path for delayed recovery: BaseRiskUnitServiceImpl.autoSyncUnitStatus() is only called at fixed daily intervals, causing a maximum delay of ~24 hours for activationTime to take effect.
5. Analyzed production data to confirm real-world examples of arbitrary stop windows (e.g., 2026-10-09 00:00:00 to 2026-10-09 11:57:26) and the resulting '24-hour delay' in task dispatch.
6. Proposed a two-part solution: condition the immediate write to only trigger if stopTime <= now, and introduce a new per-minute local state-switching task to handle future-time activations, while preserving the existing 23:30/6:00 platform sync rhythm.

## Related files
- d:\code\hc-standard\ruoyi-quartz\src\main\java\com\ruoyi\quartz\task\TaskList.java
- d:\code\hc-standard\ruoyi-system\src\main\java\com\ruoyi\base\service\impl\BaseRiskUnitLogServiceImpl.java
- d:\code\hc-standard\ruoyi-quartz\src\main\java\com\ruoyi\quartz\task\DataSyncTask.java
- d:\code\hc-standard\ruoyi-quartz\src\main\resources\mapper\quartz\TaskHandleMapper.xml
- d:\code\hc-standard\ruoyi-system\src\main\resources\mapper\base\BaseRiskTaskRecordMapper.xml

## Notes
- None

## Task overview
Successfully diagnosed the dual root cause of the 24-hour delay in risk unit start/stop timing: unconditional immediate state change on record creation and low-frequency scheduled correction. A precise, on-time solution has been designed and verified against production data, consisting of a conditioned write and a new high-frequency local task.
