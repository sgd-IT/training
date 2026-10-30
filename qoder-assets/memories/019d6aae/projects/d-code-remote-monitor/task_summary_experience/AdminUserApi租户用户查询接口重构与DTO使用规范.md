---
title: "AdminUserApi租户用户查询接口重构与DTO使用规范"
usage_scenario:
    - "When implementing or modifying an API method that serves downstream consumers like SMS notification"
    - "When refactoring an existing API to support additional fields (e.g., userId) without breaking existing callers"
    - "When diagnosing and fixing Lombok-related compilation errors in DTO construction"
keywords:
    - "AdminUserApi"
    - "AdminUserRespDTO"
    - "BeanUtils.toBean"
    - "tenantId"
    - "SMS notification"
source: "auto"
---

## Task description
- Core requirement: fix the compilation error "无法解析构造函数 'AdminUserRespDTO'" and refactor the tenant-based user query API to support both mobile number retrieval and future userId usage.
- Task background: The `getUserPhonesByTenantId` method was incorrectly typed as `List<AdminUserRespDTO>` but its implementation attempted to use `AdminUserRespDTO::new` on a `String`, while the DTO only has `@NoArgsConstructor` and a 9-parameter `@AllArgsConstructor`. The caller (`FireAlarmOrderServiceImpl`) expects `List<String>` for SMS notifications, but future needs require `userId`. The correct solution is to return full `AdminUserRespDTO` objects using the existing `BeanUtils.toBean` pattern, rename the method to reflect semantics, and let the caller extract mobile numbers.

## Execution process
1. AdminUserApi.java: renamed `getUserPhonesByTenantId` to `getUserListByTenantId`, changed return type to `List<AdminUserRespDTO>`, updated Javadoc to "根据租户编号获得启用用户列表（含 id/mobile 等）"
2. AdminUserApiImpl.java: replaced the stream-based `map(AdminUserRespDTO::new)` with `BeanUtils.toBean(users, AdminUserRespDTO.class)`, preserving tenant filtering logic and `removeIf` guard; removed unused `StrUtil` and `Collectors` imports
3. FireAlarmOrderServiceImpl.java: added `import com.enjoyiot.module.system.api.user.dto.AdminUserRespDTO`; updated the call site to `.map(AdminUserRespDTO::getMobile)` and kept `filter(StrUtil::isNotBlank)`
4. Verified all three files compile without error; remaining warnings are pre-existing null-safety issues unrelated to this change

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-system\module-system-api\src\main\java\com\enjoyiot\module\system\api\user\AdminUserApi.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\api\user\AdminUserApiImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\firewatch\FireAlarmOrderServiceImpl.java

## Notes
- None

## Task overview
Successfully refactored the tenant user query API: fixed the compilation error by abandoning the invalid constructor reference, adopted the project's standard `BeanUtils.toBean` pattern to return full DTOs, and renamed the method to accurately reflect its semantics. The change enables immediate use of mobile numbers and future access to `userId`, `nickname`, and other fields without further API modifications.
