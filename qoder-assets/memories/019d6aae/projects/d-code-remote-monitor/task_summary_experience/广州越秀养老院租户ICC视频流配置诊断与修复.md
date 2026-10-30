---
title: "广州越秀养老院租户ICC视频流配置诊断与修复"
usage_scenario:
    - "When diagnosing ICC video stream configuration issues for a specific tenant"
    - "When generating safe and executable SQL to add/update/delete camera configurations"
    - "When migrating device-camera bindings during camera configuration changes"
keywords:
    - "ICC"
    - "video stream"
    - "Guangzhou Yuexiu"
    - "tenant_id=178"
    - "camera naming"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the missing ICC video stream configuration for tenant 'Guangzhou Yuexiu Jinqiao Nursing Home' (tenant_id=178), enabling video streaming without requiring ICC platform configuration for each tenant
- Task background: user claimed 'can view cameras without ICC configuration', but current implementation requires icc_media_server record; investigation revealed the system tenant user bypasses tenant filtering, allowing access to platform ICC config; however, camera naming ('Duty Room') was incorrect, preventing display on the Mini Program 'Fire Control Room Monitoring' page

## Execution process
1. Database query: SELECT * FROM icc_media_server WHERE tenant_id = 178 (confirmed no record); SELECT * FROM video_camera WHERE tenant_id = 178 AND channel_name LIKE '%Duty Room%' (confirmed only 'Duty Room' named cameras exist)
2. Code analysis: Grep for TenantDatabaseInterceptor and TenantProperties to confirm case 3 (system tenant user bypasses tenant filtering) and verify the ignoreTables logic
3. SQL generation: Provided INSERT SQL for icc_media_server (tenant_id=178), UPDATE SQL to rename existing cameras to include 'Duty Room', and INSERT SQL for new cameras with correct naming
4. Binding migration: Diagnosed that camera_id=34 is bound to offline monitoring devices (device_camera_relation), and provided migration plan to preserve bindings when adding new records
5. Final verification: Confirmed account 174 has successful Mini Program login logs (log_type=103), proving actual access is possible

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\video\VideoCameraServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\framework\spring-boot-starter-biz-tenant\src\main\java\com\enjoyiot\framework\tenant\core\db\TenantDatabaseInterceptor.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\framework\spring-boot-starter-biz-tenant\src\main\java\com\enjoyiot\framework\tenant\config\TenantProperties.java

## Notes
- A record with tenant_id=0 was found in video_camera table (id=45, 'Guangzhou Yuexiu Nursing Home Duty Room'), which is invalid and must be cleaned up before inserting valid records for tenant 178
- The unique index uk_device_channel_id_tenant prevents duplicate (tenant_id, device_id, channel_id) combinations; physical deletion is required to reuse a combination

## Task overview
Successfully diagnosed the root cause (missing ICC config + incorrect camera naming) and delivered complete, executable SQL scripts to resolve it. The solution leverages the system tenant user's bypass capability and ensures cameras are correctly named for Mini Program filtering.
