---
title: "Dahua液压液位报警事件不对称诊断：平台解析逻辑验证与设备端行为确认"
usage_scenario:
    - "When diagnosing missing start/end event pairs in Dahua protocol integrations"
    - "When verifying whether missing events are due to device-side or platform-side issues"
keywords:
    - "Dahua协议"
    - "事件缺失"
    - "start/end事件"
    - "设备端行为"
    - "日志验证"
source: "auto"
---

## Task description
- Core requirement: diagnose why subType=26 (液压液位报警-开始) has "start" events but no corresponding "end" events (subType=27)
- Task background: DahuaEventParser.java defines both subTypes correctly; DahuaAduParser.java decodeEvent handles all event types uniformly; GbtDataReporter.java and UdpComponent.java process both TCP/UDP events without subType filtering; deduplication signature includes data_subType so start/end events generate distinct signatures

## Execution process
1. Verified DahuaEventTypeConstants.java: EVT_HYDRAULIC_LEVEL_ALARM_START=26 and EVT_HYDRAULIC_LEVEL_ALARM_END=27 are both defined ✓
2. Verified DahuaEventParser.java getSubTypeName(): case 26 → "液压液位报警(开始)", case 27 → "液压液位报警(结束)" ✓
3. Verified DahuaEventParser.java getAlarmTypeName(): both subTypes 26/27 map to the same alarmType ✓
4. Verified DahuaAduParser.decodeEvent(): delegates all event subTypes to DahuaEventParser.parse() without discrimination ✓
5. Verified GbtDataReporter.handleUniversalEvent(): reports properties and triggers universal_alarm_event for all alarmType values, no subType filtering ✓
6. Verified UdpComponent.handleDeviceReport(): processes all infoObjects in a loop with no subType filtering ✓
7. Verified dahuaSignature(): signature includes data_subType, so subType=26 and subType=27 produce different hashes and won't be deduplicated ✓
8. Concluded: platform code correctly handles both start and end events; the root cause is that the device does not send subType=27 when liquid level normalizes

## Related files
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/constants/DahuaEventTypeConstants.java
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaEventParser.java
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduParser.java
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtDataReporter.java
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java

## Notes
- None

## Task overview
Successfully diagnosed the hydraulic level alarm event asymmetry: confirmed platform parsing logic is correct for both subType=26 and subType=27; verified no filtering or deduplication occurs on the platform side; root cause is device firmware not sending subType=27 upon liquid level normalization per Dahua protocol v1.04 specification.
