---
title: "AEP烟感设备24小时心跳周期下的在线状态判定与告警策略优化"
usage_scenario:
    - "When diagnosing online/offline state inaccuracies for NB-IoT devices"
    - "When implementing reliable offline detection based on actual data reporting cycles"
    - "When fixing AEP alarm event ingestion to ensure smoke fire alarms reach the alert table"
keywords:
    - "AEP"
    - "NB-IoT"
    - "offline detection"
    - "heartbeat cycle"
    - "eventType mapping"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the online/offline state judgment logic for AEP NB-IoT smoke detectors with a 24-hour reporting cycle; implement reliable offline detection and correct alarm event ingestion.
- Task background: AEP devices report data every 24 hours, but AEP's own deviceOnlineOfflineReport causes false offline due to NB PSM sleep; the existing DeviceStateCheckTask was disabled (keepAliveTime=0); AEP eventType=1 info events were silently discarded, preventing real smoke fire alarms from reaching the alert table.

## Execution process
1. Confirmed device behavior: queried Redis iot:deviceLast:{id} and TD thing_model_message_15 to verify real reporting cadence and timestamp unit (ms).
2. Fixed offline detection: updated AEP product keep_alive_time from 0 to 86400 in DB to enable DeviceStateCheckTask; modified AepCallbackServiceImpl to drop deviceOnlineOfflineReport messages at entry to prevent PSM-induced flapping.
3. Fixed alarm ingestion: corrected AepHttpPushAlarmStrategy to handle eventType=1 as a valid fire alarm; removed dead buildOnlineOfflineMessage and parseOnlineOffline logic.
4. Verified end-to-end: confirmed deviceLast refresh on dataReport, offline timeout at ~26.4h, and correct eventType mapping in alarm records.

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AepHttpPushAlarmStrategy.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/iot/DeviceStateCheckTask.java
- /module-eiot/module-eiot-core/iot-rule-engine/src/main/java/com/enjoyiot/eiot/ruleengine/handler/sys/DeviceStateCheckHandler.java

## Notes
- AEP's deviceOnlineOfflineReport reflects modem PSM state, not device activity; it must be ignored for NB devices.
- DeviceStateCheckTask uses AEP's message timestamp (not local receive time), so AEP retransmission or device clock skew can cause stale deviceLast values.
- The 'battery_voltage' field in the smoke payload is not in the AEP thing model and is dropped by DevicePropertyHandler.

## Task overview
Successfully implemented robust online/offline state judgment for AEP smoke detectors: enabled DeviceStateCheckTask with a 24h keepAliveTime; eliminated PSM-induced state flapping by filtering deviceOnlineOfflineReport; fixed AEP alarm ingestion to capture eventType=1 fire alarms; all changes compiled and verified with real device data.
