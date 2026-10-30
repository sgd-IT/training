---
title: "AEP data report链路全链路分析与告警行为验证"
usage_scenario:
    - "When debugging why an AEP dataReport payload does not trigger an alert"
    - "When extending or modifying the AEP alarm parsing logic"
    - "When integrating new AEP device types with custom property or alarm handling"
keywords:
    - "AEP"
    - "dataReport"
    - "smoke_state"
    - "alarm chain"
    - "DevicePropertyHandler"
    - "DeviceAlertChecker"
source: "auto"
---

## Task description
- Core requirement: analyze how AEP dataReport payloads (e.g., temperature_value, smoke_state) are processed end-to-end across the system
- Task background: AEP platform pushes device data via HTTP; the system routes it through MQ, parses alarms via dual chains (AepHttpPushAlarmStrategy + DeviceAlertChecker), stores properties, and manages device online state; user needed clarity on why certain payloads don't trigger alerts

## Execution process
1. Identified THING_MODEL_MESSAGE_TOPIC consumers: RuleDeviceConsumer (rules engine), DeviceStateCheckHandler (state management)
2. Confirmed DevicePropertyHandler writes valid property fields (e.g., temperature_value, smoke_value) to Redis cache and TDengine; filtered out undefined fields (e.g., battery_voltage)
3. Verified DeviceStateCheckHandler sets device state=1 on any uplink, and DeviceStateCheckTask clears it after keepAliveTime × 1.1 timeout — online status is not driven by AEP's online/offline events
4. Analyzed AepHttpPushAlarmStrategy: only smoke_state==0 triggers fire alarm; smoke_state==1 is ignored (no alert, no WS push)
5. Analyzed DeviceAlertChecker: if configured with smoke_state conditions and enableRecover=true, smoke_state==1 triggers database recovery (handle_status=3) but no WebSocket notification
6. Confirmed dual-chain independence: AEP builtin strategy and DeviceAlertChecker operate in parallel without interference

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-core\iot-rule-engine\src\main\java\com\enjoyiot\eiot\ruleengine\handler\sys\DeviceStateCheckHandler.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-core\iot-rule-engine\src\main\java\com\enjoyiot\eiot\ruleengine\handler\sys\DevicePropertyHandler.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AepHttpPushAlarmStrategy.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-core\iot-rule-engine\src\main\java\com\enjoyiot\eiot\ruleengine\devicealert\DeviceAlertChecker.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\devicealert\DeviceAlertConfigServiceImpl.java

## Notes
- None

## Task overview
Successfully mapped the full AEP dataReport processing chain: clarified MQ routing, property storage behavior, online state mechanism, and the distinct roles of AEP builtin alarm strategy vs DeviceAlertChecker for smoke_state values; confirmed that smoke_state==1 triggers no alert but may trigger DB-only recovery when configured.
