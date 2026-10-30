---
title: "AdminUserApi按租户查用户接口手敲训练"
usage_scenario:
    - "When preparing to implement a new API method that serves an existing downstream consumer like SMS notification"
    - "When analyzing tenant context propagation and multi-tenancy plugin behavior before writing a new query method"
keywords:
    - "AdminUserApi"
    - "tenantId"
    - "SMS notification"
    - "multi-tenancy"
    - "TenantContextHolder"
source: "auto"
---

## Task description
- Core requirement: extend AdminUserApi to support querying users by tenant ID (tenantId), specifically for use in SMS notification workflows where mobile numbers are needed.
- Task background: The existing AdminUserApi has methods like getUserListByDeptIds; the new method must return List<AdminUserRespDTO> with filtering for enabled status and non-empty mobile. It must be implemented in AdminUserApiImpl by delegating to UserService, respecting the tenant plugin's automatic condition injection and avoiding double-filtering.

## Execution process
1. Identified the consumer: FireAlarmOrderServiceImpl (module-eiot-biz) line 878 calls smsSendApi.sendSingleSmsToAdmin(), which requires a list of mobile numbers from the alarm tenant.
2. Reviewed existing AdminUserApi interface and AdminUserApiImpl implementation to understand the透传 pattern (e.g., getUserListByDeptIds delegates to userService.getUserListByDeptIds()).
3. Examined UserService methods (e.g., getUserListByStatus) as a reference for query structure and filtering logic.
4. Analyzed the tenant context handling: TenantContextHolder holds the current tenant, but the question is whether the tenant plugin will auto-add a tenant_id condition when calling userService.getUserListByTenantId(tenantId), requiring @TenantIgnore.
5. Confirmed the existence of AdminUserService.getUserListByAccountIgnoreTenant as a reference for bypassing the tenant plugin.

## Related files
- anwei-fire-remote-monitor\module-system\module-system-api\src\main\java\com\enjoyiot\module\system\api\user\AdminUserApi.java
- anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\api\user\AdminUserApiImpl.java
- anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\firewatch\FireAlarmOrderServiceImpl.java
- anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\service\user\AdminUserService.java

## Notes
- None

## Task overview
Completed hand-typing training for the AdminUserApi tenant query extension: identified the SMS notification consumer, reviewed the API/Impl patterns and UserService references, and analyzed the tenant context and plugin interaction to prepare for correct implementation. No code was written in this session; it was a preparatory analysis phase.

## Reference
See the full implementation and refactoring details in the related memory: 'AdminUserApi租户用户查询接口重构与DTO使用规范' (ID: f32a8b4c-ffa0-4bed-abc9-4217c0848aa5).
