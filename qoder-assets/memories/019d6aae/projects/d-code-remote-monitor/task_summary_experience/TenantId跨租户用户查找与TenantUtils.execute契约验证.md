---
title: "TenantId跨租户用户查找与TenantUtils.execute契约验证"
usage_scenario:
    - "When implementing cross-tenant user lookup logic (e.g., SMS login)"
    - "When debugging TooManyResultsException in multi-tenant systems"
    - "When validating the correct usage pattern of TenantUtils.execute"
keywords:
    - "TenantUtils.execute"
    - "cross-tenant lookup"
    - "TooManyResultsException"
    - "multi-tenancy"
    - "tenantId"
source: "auto"
---

## Task description
- Core requirement: clarify how to correctly implement tenant-aware user lookup by tenantId, especially when multiple users share the same mobile number across tenants
- Task background: existing `getUserByMobile` is designed for single-tenant context; using it in cross-tenant scenarios (e.g., SMS login) without proper isolation causes `TooManyResultsException`; the core contract is `TenantUtils.execute` for tenant context switching

## Execution process
1. Identified garbled voice input: "零售店id" → "租户id", confirming the need for cross-tenant lookup
2. Analyzed `AdminUserMapper.selectOne()` behavior: throws `TooManyResultsException` when multiple matching users exist (verified against project's prior scheduled-task failure)
3. Confirmed `TenantUtils.execute` as the authoritative multi-tenancy contract: saves current context, sets new tenant ID, executes lambda, restores original context
4. Diagnosed the risk of misusing `getUserByMobile` directly inside `executeIgnore`: leads to 500 errors on duplicate mobiles
5. Proposed two key decisions: (A) strategy for selecting among multiple users (e.g., throw business exception vs. pick first), (B) whether to extend `getUserByMobile` or create a new list-based method

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\dal\mysql\user\AdminUserMapper.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\framework\spring-boot-starter-mybatis\src\main\java\com\enjoyiot\framework\mybatis\core\mapper\BaseMapperX.java

## Notes
- None

## Task overview
Successfully diagnosed and validated the correct approach for cross-tenant user lookup: confirmed that `selectOne` throws `TooManyResultsException` on duplicates, verified `TenantUtils.execute` as the core multi-tenancy contract, and clarified the architectural risk of reusing single-tenant methods in cross-tenant contexts. The task concluded with two critical implementation decisions still pending.

## Notes
- Verified TenantUtils.execute contract via actual code reading of AdminAuthServiceImpl.wxMaPhoneLogin and TenantUtils source.
- Confirmed TooManyResultsException behavior by reading AdminUserMapper.java line 48 and BaseMapperX.java lines 104-106.
- Clarified that misusing getUserByMobile inside executeIgnore leads to 500 errors on duplicate mobiles — this is an architectural risk, not just a theoretical concern.
