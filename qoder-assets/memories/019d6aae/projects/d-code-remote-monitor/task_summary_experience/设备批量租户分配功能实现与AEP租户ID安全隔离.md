---
title: "设备批量租户分配功能实现与AEP租户ID安全隔离"
usage_scenario:
    - "When implementing multi-tenant device management with strict immutability requirements"
    - "When integrating third-party platforms like AEP that provide tenant context but require isolation"
keywords:
    - "batch tenant assignment"
    - "tenant_id immutability"
    - "AEP tenant isolation"
    - "device management"
source: "auto"
---

## Task description
- Core requirement: implement a batch tenant assignment feature for devices, ensuring tenant_id is immutable once assigned and preventing direct storage of AEP platform's tenantId
- Task background: AEP pushes device data without tenant context; super admin must later assign tenants from the tenant table; existing AepCallbackServiceImpl incorrectly used AEP's tenantId, violating the security constraint

## Execution process
1. Created DeviceBatchAssignTenantReqVO to define the batch assignment request structure
2. Added batchAssignTenant method to DeviceInfoService interface and implemented it in DeviceInfoServiceImpl with @TenantIgnore annotation and WHERE tenant_id = 0 condition to enforce immutability
3. Added POST /eiot/device/batchAssignTenant controller endpoint in DeviceInfoController
4. Updated AepCallbackServiceImpl to use @TenantIgnore instead of extracting tenantId from AEP message body, ensuring devices are created with tenant_id=0 (unassigned)
5. Verified the SQL UPDATE statement enforces immutability by only updating records where tenant_id = 0

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/admin/device/vo/DeviceBatchAssignTenantReqVO.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/device/DeviceInfoService.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/device/DeviceInfoServiceImpl.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/admin/device/DeviceInfoController.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java

## Notes
- Initial implementation over-engineered the productId↔productKey mapping; user guidance simplified it to direct usage
- TenantSecurityWebFilter operates at HTTP filter level before controller, so tenant context cannot be set from message body — ignore-urls is the only safe and correct fix

## Task overview
Successfully implemented a secure batch tenant assignment system: devices are created unassigned (tenant_id=0) via AEP push; super admin can batch assign tenants via new API; database-level constraint (WHERE tenant_id = 0) guarantees immutability; AEP's tenantId is never stored directly.
