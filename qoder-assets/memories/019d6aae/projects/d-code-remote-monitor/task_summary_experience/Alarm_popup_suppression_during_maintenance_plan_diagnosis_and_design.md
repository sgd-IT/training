---
title: "Alarm popup suppression during maintenance plan diagnosis and design"
usage_scenario:
    - "Diagnosing incomplete backend feature implementation"
    - "Designing cross-chain alarm suppression logic"
keywords:
    - "alarm popup"
    - "maintenance plan"
    - "suppression"
    - "FireMaintenancePlanDO"
    - "TenantUtils.executeIgnoreWithResult"
source: "auto"
---

## Task description
- Core requirement: diagnose whether alarm popup suppression during maintenance plans is implemented, and clarify the design for its implementation
- Task background: frontend has added tenant/time fields to maintenance plan form; backend partially implemented VO/DO but with compilation errors; no alarm chain integration yet; three alarm chains (TCP/UDP/AEP, camera, rule engine) need to be modified to check maintenance windows and suppress popups

## Execution process
1. Confirmed frontend completion of tenant/time fields and validation in plans.vue
2. Grep'd backend files to verify absence of maintenance logic in alarm/devicealert/alert packages
3. Read FireMaintenancePlanDO and FireMaintenancePlanServiceImpl to identify compilation errors: missing UserTenantResolver import, invalid .targetTenantId() builder call, missing startTime field, and hard-coded endTime
4. Verified EnjoyJacksonAutoConfiguration does not override FAIL_ON_UNKNOWN_PROPERTIES, confirming Spring Boot default behavior (ignore unknown fields)
5. Designed unified maintenance window service using TenantUtils.executeIgnoreWithResult to bypass tenant filtering, with SQL query filtering by target_tenant_id, start_time/end_time, and plan_status <> 'completed'
6. Proposed integration points for all three alarm chains: set alarm_kind=7, handle_status=3, read_flg=true when suppressed, and skip WebSocket push for KIND_MAINTENANCE
7. Clarified that frontend popup logic requires no changes because suppression is handled at the backend alarm ingestion level

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\controller\admin\firewatch\vo\plan\PlanCreateReqVO.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\dal\dataobject\firewatch\FireMaintenancePlanDO.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\firewatch\FireMaintenancePlanServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\framework\spring-boot-starter-web\src\main\java\com\enjoyiot\framework\jackson\config\EnjoyJacksonAutoConfiguration.java

## Notes
- Backend compilation errors prevent plan saving from working: missing import, invalid builder usage, and missing startTime field
- TenantUtils.executeIgnoreWithResult is required for the maintenance window query because fire_maintenance_plan rows have tenant_id = creator, not target_tenant_id
- Alarm suppression must exclude completed plans; user confirmed 'completed' status immediately restores popup
- No cache is needed for the maintenance window query; direct DB lookup with proper index is sufficient and avoids TTL delay issues

## Task overview
Diagnosis confirmed alarm popup suppression is not implemented. Design clarified: three alarm chains must query the maintenance plan table (using target_tenant_id and time range) and suppress popups by setting alarm_kind=7 and skipping WebSocket push. Backend plan saving is currently broken due to compilation errors and requires fixing before suppression can be tested.
