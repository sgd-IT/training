---
title: "广州越秀金桥养老院摄像头及ICC配置删除操作归档"
usage_scenario:
    - "When needing to safely remove a tenant's camera and ICC configurations from the production database"
    - "When troubleshooting or reverting a misconfigured camera setup for a specific tenant"
keywords:
    - "广州越秀金桥养老院"
    - "摄像头删除"
    - "ICC配置删除"
    - "physical delete"
    - "unique index"
source: "auto"
---

## Task description
- Core requirement: delete the camera configuration and ICC configuration for tenant 'Guangzhou Yuexiu Jinqiao Nursing Home' (tenant_id=178)
- Task background: the user previously added 5 camera channels (device_id=1000032, channel_id=0-4) and an ICC media server configuration for this tenant; now requests to remove them, likely due to misconfiguration or business decision

## Execution process
1. Confirmed current state: queried video_camera table and confirmed tenant 178 has 5 active records (id=35-39) for device 1000032, and icc_media_server table has one active record (id=3)
2. Analyzed deletion strategy: clarified that MCP is read-only (ALLOW_DELETE_OPERATION=false), so physical DELETE SQL must be executed by the user; explained why physical deletion (not logical) is recommended to avoid unique index conflicts on future re-insertion
3. Provided complete SQL scripts: included backup (CREATE TABLE AS SELECT), physical deletion for both video_camera and icc_media_server, and verification queries
4. Highlighted critical considerations: warned about the impact of deleting id=34 (which is bound to offline monitoring cameras) and emphasized that the provided SQL only deletes the newly added 5 channels (1000032)

## Related files
- C:\Users\Administrator\.qoder\mcp.json
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\video\VideoCameraServiceImpl.java

## Notes
- The MCP instance anwei-fire-remote-monitor-prod has ALLOW_DELETE_OPERATION=false, so all database modifications must be performed directly by the user via SQL in a database client
- Physical deletion is required because the unique index uk_device_channel_id_tenant does not include the 'deleted' column; logical deletion would block future re-insertion of the same (tenant_id, device_id, channel_id) combination
- The provided SQL targets only the newly added device 1000032; if the user wants to delete the original device 1000047 (id=34), it must be done separately with awareness of its binding to offline monitoring

## Task overview
Successfully delivered a complete, safe, and executable plan to delete the camera and ICC configurations for tenant 178. The plan includes backup, targeted physical deletion, and verification steps, along with clear warnings about potential impacts and the necessity of physical deletion.
