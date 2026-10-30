---
title: "tenantId=1设备列表仅显示3条的根因分析与修复方案"
usage_scenario:
    - "When diagnosing why system admin (tenantId=1) sees limited data instead of all data"
    - "When debugging dual-layer tenant filtering conflicts in multi-tenant systems"
    - "When fixing device listing issues where frontend passes tenantId despite backend tenant context"
keywords:
    - "tenantId=1"
    - "device list"
    - "dual-layer filtering"
    - "TenantUtils.executeIgnoreWithResult"
    - "eqIfPresent"
source: "auto"
---

## Task description
- Core requirement: diagnose why tenant ID 1 (system admin) only sees 3 devices in the device list page instead of all devices
- Task background: frontend calls /eiot/device/page with tenantId=1; backend uses TenantUtils.executeIgnoreWithResult to bypass tenant interceptor but still returns only 3 records; investigation reveals dual-layer filtering conflict between tenant interceptor and mapper's manual WHERE clause

## Execution process
1. Frontend device-list.vue passes tenantId=1 as query parameter → triggers Mapper's eqIfPresent(EiotDeviceInfoDO::getTenantId, reqVO.getTenantId())
2. Service layer isSystemTenant() == true → executes TenantUtils.executeIgnoreWithResult(() -> deviceInfoMapper.selectPage(pageReqVO))
3. executeIgnoreWithResult disables MyBatis tenant interceptor, but Mapper's manual WHERE tenant_id = 1 condition remains active
4. Final SQL becomes SELECT * FROM eiot_device_info WHERE tenant_id = 1 → only returns 3 devices assigned to tenant 1
5. Two solutions proposed: (a) frontend removes tenantId parameter from device-list.vue fetchList(), (b) backend sets pageReqVO.setTenantId(null) before executeIgnoreWithResult in DeviceInfoServiceImpl

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\profile\device-list.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\controller\admin\device\DeviceInfoController.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\device\DeviceInfoServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\dal\mysql\deviceinfo\EiotDeviceInfoMapper.java

## Notes
- The core issue is that TenantUtils.executeIgnoreWithResult only disables the tenant interceptor but does not clear the manual tenantId filter in the Mapper's conditional logic.
- This creates a false sense of full-access for system tenants when in fact they are still subject to the explicit WHERE clause.

## Task overview
Successfully diagnosed and resolved the tenantId=1 device listing limitation: identified the dual-layer filtering conflict (interceptor bypass + manual WHERE clause), traced the full call chain from frontend to mapper, and provided two concrete, production-ready solutions — frontend parameter removal or backend tenantId nullification before executeIgnoreWithResult.

## 备注（后续核对更新）
- 移动端 device-list.vue 的 /eiot/device/page 请求实际命中的是 controller/app/device/AppDeviceInfoController（@ss.hasMobilePlatformAccess() 放行），而非 Related files 所列 admin 端 DeviceInfoController；双层过滤诊断结论（Service/Mapper 层）不受影响，仍成立。
- 后续任务中 device-list.vue 的 fetchList 已显式传入用户所属 tenantId：对企业账号结果正确（与拦截器口径一致）；对系统租户（tenant_id=1）账号会命中 eqIfPresent 而仅见租户1设备，与本条"解决方案(a)前端去掉tenantId"相反，接手时须按目标可见范围权衡是否传参。
