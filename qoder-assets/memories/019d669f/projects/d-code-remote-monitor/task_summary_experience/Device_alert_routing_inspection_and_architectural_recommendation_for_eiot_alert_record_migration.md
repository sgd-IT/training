---
title: "Device alert routing inspection and architectural recommendation for eiot_alert_record migration"
usage_scenario:
    - "When inspecting or debugging device alert routing logic across module boundaries"
    - "When diagnosing compilation errors related to cross-module DTO/DO usage"
keywords:
    - "device alert"
    - "duty-screen"
    - "eiot_alert_record"
    - "module dependency"
    - "AlertRecordDO"
source: "auto"
---

## Task description
- Core requirement: inspect and unify device alert routing to trigger duty-screen popups via eiot_alert_record + WS firewatch-alert-new
- Task background: current implementation writes to eiot_device_alert_record; target is to write to eiot_alert_record with WebSocket push; requires verifying module dependencies, interface signatures, and cross-module visibility rules

## Execution process
1. Read DeviceAlertConfigServiceImpl.java (314 lines) and compared with prior version (303 lines) to identify new addAlertRecord method and removed deviceManagerService field
2. Grep project for addAlertRecord calls and DeviceApi interface definition, revealing DeviceApiImpl L205-207 calling addAlertRecord and DeviceApi.java L96 declaring AlertRecordDo parameter
3. Read DeviceApi.java and DeviceAlertAction.java to verify signature mismatch: AlertRecordDo (interface) vs AlertRecordDO (actual class) and DeviceAlertRecord (Action's payload)
4. GetProblems confirmed 4 compilation errors: AlertRecordDo unresolved type, unimplemented interface method, and signature mismatch in DeviceAlertAction
5. Diagnosed root cause: AlertRecordDO (biz/dal) cannot appear in DeviceApi (api) interface signature due to module dependency constraints
6. Identified functional gap: addAlertRecord lacks notifyAlarmSaved call → duty-screen won't popup even if compiled
7. Analyzed side effects of switching to alert-only writing: de-duplication, recovery, and management page queries all break because they rely on eiot_device_alert_record

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\devicealert\DeviceAlertConfigServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\devicealert\DeviceAlertConfigService.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-api\src\main\java\com\enjoyiot\module\eiot\api\device\DeviceApi.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\api\device\DeviceApiImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-core\iot-rule-engine\src\main\java\com\enjoyiot\eiot\ruleengine\devicealert\DeviceAlertAction.java

## Notes
- None

## Task overview
Completed inspection of device alert routing changes: identified 4 compilation errors caused by AlertRecordDo/AlertRecordDO naming mismatch and module boundary violation; diagnosed architecture constraint (dal DO cannot cross into api interface); found missing notifyAlarmSaved call preventing duty-screen popup. Inspection stage had recommended Route A (keep dual-write), but the final decision rejected dual-write: device alerts are written uniformly into eiot_alert_record and eiot_device_alert_record is stopped at code level, with de-duplication migrated to AlertRecordMapper.selectActiveAlert and recovery closing the loop via handle_status=3.
