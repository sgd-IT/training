---
title: "Special work video upload failure diagnosis and root cause analysis"
usage_scenario:
    - "当调试LuoboXiang服务因workStartTime/workEndTime时间相等导致的3012错误时"
    - "当实现或修改特殊作业票的时间校验逻辑以避免视频上传失败时"
keywords:
    - "special work"
    - "video upload"
    - "regulatory platform"
    - "LuoboXiang"
    - "3012 error"
source: "auto"
---

## Task description
- Core requirement: diagnose why the special work ticket ZY-626082711062079 displays "暂未上传，无法预览" on the regulatory platform
- Task background: the ticket is in 'in progress' status (workStatus=2); video upload is triggered at startWork; the platform preview fails when no video or ticket file is available; systemFileIds are only generated at endWork, not startWork

## Execution process
1. Searched frontend code for "暂未上传" and "无法预览" — no matches found in local ruoyi-ui or Android app
2. Read BaseWorkSpecialServiceImpl.java to confirm systemFileIds is only set in endWork() via printWorkSpecial(), not in startWork()
3. Analyzed sys-info.log to trace the video push timeline: first push at 11:08:28 returned data:false; second push at 11:08:40 (after refreshVideo) returned data:true
4. Identified that workStartTime and workEndTime are both 11:03:00, which causes LuoboXiang saveTicketAndDevice to return 3012 error: "作业结束时间不能早于或等于作业开始时间"
5. Confirmed the 3012 error prevents successful video upload on first attempt

## Related files
- /service/specialwork/impl/BaseWorkSpecialServiceImpl.java

## Notes
- Critical data issue: workStartTime must be strictly earlier than workEndTime; identical timestamps trigger 3012 error in LuoboXiang service, blocking video upload
- Updated confirmation from follow-up task:
  - Confirmed requirement is backend-only: only modify BaseWorkSpecialServiceImpl.java's startWork() method; App side remains unchanged.
  - Reinforced architectural constraint: systemFileIds are only set in endWork(), not startWork(); this confirms video upload logic must be decoupled from ticket file generation.
  - Reinforced root cause validity: identical timestamps continue to trigger LuoboXiang 3012 error, confirming strict time validation is mandatory before any video upload or LuoboXiang integration call.

## Task overview
Diagnosed root cause of "暂未上传，无法预览": first video push failed (data:false) because LuoboXiang service rejected the ticket due to identical workStartTime and workEndTime (3012 error); resolution requires enforcing strict time validation before video push initiation.

## Related Memory
- See 'LuoboXiang saveTicketAndDevice 3012 error from identical work start/end times' for the core pitfall and fix pattern.
