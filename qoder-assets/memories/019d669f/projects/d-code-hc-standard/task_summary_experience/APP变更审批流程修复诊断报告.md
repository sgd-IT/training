---
title: "APP变更审批流程修复诊断报告"
usage_scenario:
    - "When diagnosing APP端审批流程异常问题"
    - "When debugging special work ticket signature flow"
    - "When identifying root causes for workflow deadlocks in RuoYi projects"
keywords:
    - "APP审批"
    - "变更审批"
    - "特殊作业票"
    - "会签流程"
    - "BaseWorkSpecialServiceImpl"
    - "BaseWorkStepMapper"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the APP端 approval flow anomaly for 'APP变更审批流程修复' (requirement #54)
- Task background: The mobile app's approval flow for change requests is broken; change applications cannot flow normally. The Debug subagent identified that the 'change approval' functionality corresponds to the special work ticket (8 types) signature workflow and the 'change operator' action, which can only be performed via the APP.

## Execution process
1. Debug subagent launched to systematically search for the approval workflow in the codebase (other1 Android project, ruoyi-admin, ruoyi-ui).
2. Identified two critical code-level defects:
   - Defect A: In BaseWorkSpecialServiceImpl.java (L97-99), non-current operators have their workStatus forcibly masked to "1" (completed), hiding all buttons including 'assign operator', causing a deadlock.
   - Defect B: In BaseWorkStepMapper.xml (L53-55), the SQL exclusion condition for safety measures fails because it requires exact match on '其他安全措施：', preventing 'start work' after all signatures are complete.
3. Discovered that the 'return/rollback' function is completely missing from both APP and backend.

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/system/service/impl/BaseWorkSpecialServiceImpl.java
- /ruoyi-system/src/main/resources/mapper/system/BaseWorkStepMapper.xml

## Notes
- None

## Task overview
Successfully diagnosed the root cause of the APP变更审批流程 failure: two confirmed code defects (status masking and SQL exclusion) prevent normal workflow progression, and the 'return' functionality is entirely absent. A structured repair plan was formulated, prioritizing defect A and B fixes before addressing the missing 'return' feature.
