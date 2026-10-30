---
title: "告警全链路贯通验证与tenant_id修复"
usage_scenario:
    - "当需要修复多租户系统中告警归属错误时参考此修复方案"
    - "当演示前需验证告警全链路贯通性时复用此测试流程"
keywords:
    - "AEP告警"
    - "tenant_id修复"
    - "AlarmRecordService"
    - "WebSocket推送"
    - "工单流转"
source: "auto"
---

## Task description
- Core requirement: verify and fix the end-to-end alarm chain from AEP callback to frontend popup, specifically addressing the tenant_id=0 bug that breaks multi-tenancy filtering
- Task background: AEP callbacks trigger alarms but store with tenant_id=0, causing tenant 166 users to see popups but no records in the alert center; the root cause is @TenantIgnore on the outer method preventing tenant context propagation to AlarmRecordService

## Execution process
1. AepCallbackServiceImpl.java: wrap the alarm parsing and storage logic inside TenantUtils.execute(deviceInfo.getTenantId(), ...) to restore tenant context for correct tenant_id population
2. Compile verification: set JAVA_HOME to IDEA's JBR 21 and run mvn compile -pl module-eiot/module-eiot-biz -am -DskipTests to confirm compilation success
3. Chain validation: POST /admin-api/eiot/aep/product/smoke/callback with eventType=3 (fault) to trigger an alarm; verify new record in eiot_alert_record has tenant_id=166
4. Frontend integration: confirm duty-screen and monitoring-center listen to 'alarm-push' WebSocket messages and display popups
5. Work order flow: verify FireAlarmOrderController endpoints (/create, /handle, /confirm, etc.) are fully functional for alarm disposition

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java

## Notes
- The 5-minute deduplication window requires changing device or alarm type for consecutive demonstrations
- GBT26875 TCP component (port 8103) is active but does not feed into the unified alarm chain (no TcpGbtAlarmStrategy implementation)

## Task overview
Successfully verified and fixed the AEP alarm chain: tenant_id=0 bug was identified and resolved by restoring tenant context via TenantUtils.execute; compilation passed; real-time alarm creation and popup display were validated; work order disposition endpoints are confirmed ready for demonstration.

## Notes
- 「Frontend integration 仅确认 alarm-push」这一表述已过时：duty-screen / monitoring-center 现同时监听 firewatch-alert-new 消息类型，且该消息由统一门面 AlarmRealtimeNotifyService.notifyAlarmSaved 发出，调用方不再限于 AlarmRecordService（AEP 路径）——规则引擎 AlertMessageEventListener 与摄像头 reportCameraAlert 也各自推送，故同一条排查不能再假设"只有 AEP 会实时弹窗"。
- 「5分钟去重窗口」仅存在于 AlarmRecordService.saveAlarms（AEP 路径，内存 ConcurrentHashMap，key=deviceId:alarmType:sourceType）；摄像头 reportCameraAlert 路径原本无任何幂等保护，这正是同一事件成对入库的原因。
