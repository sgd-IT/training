---
title: "Hand-typing-training code review for alarm notification feature"
usage_scenario:
    - "When reviewing hand-typed code for alarm notification features"
    - "When diagnosing cross-module dependency violations in eiot-biz"
    - "When verifying TenantUtils.execute usage against project multi-tenancy contract"
keywords:
    - "hand-typing-training"
    - "code review"
    - "alarm notification"
    - "TenantUtils.execute"
    - "cross-module violation"
source: "auto"
---

## Task description
- Core requirement: review hand-typed code implementing alarm notification (tenant-based phone number retrieval and SMS dispatch)
- Task background: user hand-typed changes across 6 files including AlertApiImpl.java, FireAlarmOrderServiceImpl.java, and application.yaml; the implementation integrates phone retrieval into the existing confirmAlarm SMS path rather than building a new alarm-generated path; key project constraints include TenantUtils.execute multi-tenancy contract, cross-module dependency boundaries, and configuration integrity

## Execution process
1. Read AlertApiImpl.java and ran git status/diff to identify all changed files (6 files, 180+ insertions)
2. Reviewed FireAlarmOrderServiceImpl.java to locate the new notify logic in sendConfirmSmsNotification, confirming it reuses the existing confirmAlarm path
3. Verified correct usage of TenantUtils.execute(tenantId) for tenant-scoped user queries, matching the project's multi-tenancy contract
4. Diagnosed P0 issues: unused adminUserMapper field injection violating eiot-biz → system-biz DAL layer boundary; application.yaml config block removal breaking SSO and callpush functionality
5. Confirmed compilation success (zero errors, only pre-existing null-safety warnings)

## Related files
- /module-system/module-system-api/src/main/java/com/enjoyiot/module/system/api/user/AdminUserApi.java
- /module-system/module-system-biz/src/main/java/com/enjoyiot/module/system/api/user/AdminUserApiImpl.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireAlarmOrderServiceImpl.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/api/alert/AlertApiImpl.java
- /server/src/main/resources/application.yaml

## Notes
- The hand-typed code places phone retrieval inside the confirmAlarm notification path, not the alarm-generation path — this is a product semantic choice that must be confirmed with the user
- Cross-module import of system-biz's AdminUserMapper into eiot-biz violates project architecture and must be removed
- application.yaml modifications must be stripped before commit to avoid breaking SSO and callpush

## Task overview
Partially completed: core phone retrieval logic implemented and compiled successfully; however, P0 architectural violations (cross-module import) and config breakage (application.yaml) remain unresolved. The notification path placement (confirm vs generate) requires user confirmation to align with product intent.
