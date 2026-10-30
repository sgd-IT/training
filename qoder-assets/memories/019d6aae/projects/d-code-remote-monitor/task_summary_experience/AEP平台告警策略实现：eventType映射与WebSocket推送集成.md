---
title: "AEP平台告警策略实现：eventType映射与WebSocket推送集成"
usage_scenario:
    - "When adding alarm parsing capability for a new upstream IoT platform like AEP"
    - "When extending the alarm strategy factory pattern to handle new message types and fields"
    - "When integrating alarm storage with real-time WebSocket push for frontend notification"
keywords:
    - "AEP"
    - "alarm strategy"
    - "eventType"
    - "WebSocket push"
    - "AlarmParseStrategy"
source: "auto"
---

## Task description
- Core requirement: Implement AEP alarm parsing strategy based on eventReport's eventType field (1=info, 2=warning, 3=fault) and integrate with WebSocket real-time push.
- Task background: Existing AepCallbackServiceImpl handles HTTP push but lacks alarm parsing; AlarmRecordService stores alarms but doesn't trigger real-time push; AlarmPopup is disabled in duty-screen. The goal was to close the full chain from AEP event → alarm record → WebSocket → frontend popup.
- New requirement: Also handle AEP heartbeat messages to maintain accurate device online status.

## Execution process
1. Created AepHttpPushAlarmStrategy.java: implements AlarmParseStrategy interface; maps eventReport eventType=2→alarmType=risk/alarmLevel=warning, eventType=3→alarmType=fault/alarmLevel=major; maps deviceOnlineOfflineReport eventType=0→alarmType=offline/alarmLevel=warning.
2. Modified AepCallbackServiceImpl.java: injected AlarmStrategyFactory and AlarmRecordService; added processAlarmIfNeeded() that constructs AlarmParseContext with aepMessageType and aepEventType from eventParams; calls factory.parse() and AlarmRecordService.saveAlarms() after MQ publish.
3. Verified integration: all changes compiled successfully; eventType mapping logic confirmed correct per task requirement.

## Related files
- /module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AepHttpPushAlarmStrategy.java
- /module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java

## Notes
- None

## Task overview
Successfully implemented the AEP alarm strategy: created a dedicated strategy class for eventType-based parsing; integrated it into the callback service via a new processAlarmIfNeeded method; ensured correct mapping of eventType values to alarmType and alarmLevel; all changes verified and compiled without error.
