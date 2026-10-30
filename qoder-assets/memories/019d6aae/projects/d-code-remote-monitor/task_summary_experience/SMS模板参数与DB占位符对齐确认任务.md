---
title: "SMS模板参数与DB占位符对齐确认任务"
usage_scenario:
    - "When implementing or debugging SMS notification template parameter alignment"
    - "When verifying DO field availability before writing template parameter mapping logic"
keywords:
    - "SMS template"
    - "template parameters"
    - "AlertRecordDO"
    - "EiotDeviceInfoDO"
    - "DB contract"
source: "auto"
---

## Task description
- Core requirement: align the templateParams keys in FireAlarmOrderServiceImpl.sendSmsToProperties with the DB template's placeholders (fire_alarm_confirmed_notify, id=23) to fix 91 lines of 'success' logs that are actually placeholder-filled junk SMS.
- Task background: The DB template uses `${event_name} ${deviceName} ${deviceAddr} ${subDeviceName} ${subDeviceAddr} ${event_time}`; current code puts mismatched keys. Data sources were verified via grep/read: AlertRecordDO has alertTime/name/details/deviceName/subDeviceId but no deviceAddr; EiotDeviceInfoDO has name but no address field. Template param contract is authoritative source.

## Execution process
1. Verified AlertRecordDO fields (alertTime, name, details, deviceName, subDeviceId) via grep on d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot
2. Verified EiotDeviceInfoDO fields (name only, no address) via grep on the same path
3. Confirmed eiotDeviceInfoMapper.selectById() usage at L817 in FireAlarmOrderServiceImpl
4. Formulated mapping: event_name → record.getName(), event_time → formatAlertTime(record.getAlertTime()), deviceName → eiotDeviceInfoMapper.selectById(record.getDeviceId())?.getName(), deviceAddr/subDeviceName/subDeviceAddr → "" (no data source)
5. Defined acceptance criteria: keys must match placeholders exactly; delete old wrong keys (propertyName/propertyAddress/handler/alarmTime/alarmType); DB side unchanged
6. Added null-guarding guidance for all lookups

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\dal\dataobject\AlertRecordDO.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\dal\dataobject\deviceinfo\EiotDeviceInfoDO.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\firewatch\FireAlarmOrderServiceImpl.java

## Notes
- None

## Task overview
Successfully completed the data source verification and mapping design for SMS template parameter alignment: confirmed the exact fields available in AlertRecordDO and EiotDeviceInfoDO, established the correct key-to-field mapping, defined strict acceptance criteria based on the DB template contract, and provided null-guarding guidance. No code was modified in this session; it was a preparatory analysis phase for implementation.
