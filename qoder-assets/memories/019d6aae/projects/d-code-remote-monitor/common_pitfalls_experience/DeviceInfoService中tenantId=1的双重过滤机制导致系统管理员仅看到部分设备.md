---
title: "DeviceInfoService中tenantId=1的双重过滤机制导致系统管理员仅看到部分设备"
usage_scenario:
    - "When debugging why system admin (tenantId=1) sees limited data instead of all data"
    - "When system admin sees other tenants' records or duplicated cross-tenant rows"
    - "When implementing tenant-aware endpoints that must handle system admin bypass correctly"
keywords:
    - "tenantId=1"
    - "dual-layer filtering"
    - "TenantUtils.executeIgnoreWithResult"
    - "eqIfPresent"
    - "TenantDatabaseInterceptor ignoreTable"
    - "system admin cross-tenant visibility"
source: "auto"
---

Bug class: multi-tenancy visibility misconfiguration
Root cause: When TenantContextHolder.getTenantId() returns 1L (system admin), DeviceInfoService.getDeviceInfoPage uses TenantUtils.executeIgnoreWithResult to disable MyBatis-Plus tenant interceptor, but the mapper's eqIfPresent(EiotDeviceInfoDO::getTenantId, reqVO.getTenantId()) still applies a WHERE tenant_id = ? filter if reqVO.tenantId is non-null, causing only devices assigned to tenant 1 to be returned.
Fix pattern: Either remove the tenantId parameter from frontend device-list.vue fetchList(), or set pageReqVO.setTenantId(null) before calling TenantUtils.executeIgnoreWithResult in DeviceInfoServiceImpl.
Reusable lesson: Don't assume TenantUtils.executeIgnoreWithResult fully bypasses all tenant filtering because the mapper's manual WHERE clause remains active; instead explicitly clear the tenantId parameter before executeIgnoreWithResult. Applies when debugging why system admin sees limited data instead of all data; does not apply to ordinary tenant logins.
