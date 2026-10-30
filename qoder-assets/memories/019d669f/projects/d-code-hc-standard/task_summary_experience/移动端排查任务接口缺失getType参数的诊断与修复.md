---
title: "移动端排查任务接口缺失getType参数的诊断与修复"
usage_scenario:
    - "当移动端出现'数据异常'等模糊报错时进行根因诊断"
    - "当需要为后端API添加缺省值以兼容旧版客户端时"
    - "当需要构建新APK并验证其功能正确性时"
keywords:
    - "移动端"
    - "数据异常"
    - "getType"
    - "排查任务"
    - "APK构建"
    - "后端兜底"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the '数据异常' popup that appears when clicking '风险排查' in the mobile app.
- Task background: The popup is caused by a MissingServletRequestParameterException on the `/api/risk/getTaskRecordList` endpoint, indicating the required `getType` parameter is missing from the request. This task involved a multi-step investigation to identify the root cause (an outdated APK) and implement both client-side and server-side fixes.

## Execution process
1. Identified the backend error log and confirmed the missing `getType` parameter.
2. Located the APP-side caller (`ActivityRiskManagement.java`) and verified it sends `getType` in the current source code and compiled class.
3. Diagnosed the root cause: the tester's phone was running an older APK build that predates the `getType` parameter addition.
4. Performed a full verification of the current codebase's correctness, including confirming the multipart form submission pattern works for other endpoints (e.g., login).
5. Built a new APK (`app-debug.apk`) from the current source to resolve the client-side issue.
6. Hardened the backend by adding `defaultValue = "-1"` to the `@RequestParam` annotation in `ApiRiskController.java` to provide a safe fallback for old clients.
7. Verified the backend change compiles successfully with offline Maven.
8. Updated two related project architecture memories to improve their recall precision for future similar tasks.

## Related files
- /other1/app/src/main/java/com/sckj/other1/activity/ActivityRiskManagement.java
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/ApiRiskController.java
- /other1/app/build.gradle
- /ruoyi-common/src/main/java/com/ruoyi/common/filter/RepeatableFilter.java

## Notes
- None

## Task overview
Successfully diagnosed and resolved the '数据异常' popup: confirmed the root cause was an outdated mobile APK; built a new APK from the current source; added a robust default value to the backend API to prevent the error for any remaining old clients; updated relevant architectural memories for better future recall.

## Notes
- 本条只记录了 getType 参数缺失的报错修复，未记录 getType 各取值与任务状态的对应关系。后续核实补充：getType=0 对应 check_status=0（待检）、getType=1 对应 check_status in (1,2,3)（已检：正常/异常/不涉及）、getType=4 对应 check_status=4（已逾期），三个 tab 互斥，共同硬条件为 deleted=0 AND create_time<=NOW()，另按 check_user_id LIKE '%userId%' 过滤当前登录人。故"逾期任务在移动端也能看到"的答案是肯定的，但必须切到逾期 tab，不会与待检任务混排；日常任务与包保任务走同一套列表机制，包保身份仅在详情页由 is_defend=='1' 显示。
