---
title: "AEP报警推送链路端到端实现"
usage_scenario:
    - "When debugging or extending the AEP alarm push functionality"
    - "When integrating new device types with the AEP platform"
    - "When troubleshooting missing or duplicate duty-screen popups"
keywords:
    - "AEP"
    - "alarm push"
    - "firewatch-alert-new"
    - "eiot_alert_record"
    - "duty-screen"
source: "auto"
---

## Task description
- Core requirement: understand and document the complete AEP alarm push chain from upstream AEP platform to downstream duty-screen popup
- Task background: AEP (China Telecom's IoT platform) pushes alarms via HTTP POST; the system must route them to eiot_alert_record for persistence and firewatch-alert-new WebSocket for real-time duty-screen notification; previous memories indicated dual-write was rejected and full migration to eiot_alert_record was required

## Execution process
1. AepCallbackController.java: receives product-level callbacks at /eiot/aep/product/smoke/callback, parses raw JSON into AepPushMessage, and returns immediate "success" to prevent AEP retries
2. AepCallbackServiceImpl.java: asynchronously processes messages, ignoring deviceOnlineOfflineReport (due to NB-IoT PSM state jitter), and routes dataReport/eventReport to ThingModelMessage → MQ → Alarm pipeline
3. AepHttpPushAlarmStrategy.java: parses smoke_state=0 as critical fire alarm, error/fault codes as warnings/major faults, and tamper/humidity/temperature states as specific alerts
4. AlarmRecordService.java: saves alarms to eiot_alert_record with 5-minute Redis-based de-duplication on {deviceId}:{alarmType}:{sourceType}; triggers dual-channel WebSocket push upon success
5. AlarmRealtimeNotifyService.java: filters info-level alerts and broadcasts firewatch-alert-new to both alert tenant and system tenant (tenant_id=1)
6. duty-screen/index.vue: listens for firewatch-alert-new, deduplicates by alertId, and displays popups with voice announcements

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\controller\admin\aep\AepCallbackController.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\aep\AepCallbackServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AepHttpPushAlarmStrategy.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AlarmRecordService.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AlarmRealtimeNotifyService.java
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\src\views\eiot\firewatch\duty-screen\index.vue

## Notes
- Online status is not determined by AEP's online/offline report but by a custom keep-alive mechanism: DeviceStateCheckHandler sets state=1 on any uplink, and DeviceStateCheckTask clears it after 1.1×keepAliveTime timeout
- The 5-minute de-duplication cache is now implemented using Redis, making it shared across instances and persistent across restarts.

## Task overview
Successfully documented the complete AEP alarm push chain: clarified the callback entry, message routing logic, alarm parsing rules, database persistence with de-duplication, dual-channel WebSocket broadcasting, and frontend consumption. All key components were verified through code reading and cross-referencing.

## 更正（后续代码核实）
- 第 4 条与 Notes 中的"Redis 去重"不准确：5 分钟去重缓存实际为单机内存实现（AlarmRecordService 内 ConcurrentHashMap，{deviceId}:{alarmType}:{sourceType}），多实例部署时各实例独立去重、服务重启后清空；若后续多实例部署需改造为 Redis。
