---
title: "漏铝设备dataStatus被定时任务误判离线的修复"
usage_scenario:
    - "When diagnosing why device status is automatically reset to 0"
    - "When modifying scheduled jobs that affect device online/offline status"
keywords:
    - "updateGateWayStatus"
    - "漏铝设备"
    - "dataStatus"
    - "xxl-job"
    - "em_device_realtime"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix why leak aluminum devices' dataStatus always reverts from 1 to 0
- Task background: user reported that setting dataStatus=1 for leak aluminum devices (device_category='监测漏铝') in em_aluminum_device_info table always gets reverted to 0 by a scheduled job; investigation revealed the xxl-job updateGateWayStatusHandler calls AluminumDeviceInfoServiceImpl.updateGateWayStatus(), which scans all devices and sets dataStatus=0 if no recent data is found in em_device_realtime; leak aluminum devices do not report via MQTT, so they have zero records in em_device_realtime, causing false negatives

## Execution process
1. Confirmed production database has '监测漏铝' category devices with dataStatus=0 and zero matching gateway_num entries in em_device_realtime
2. Identified the root cause: updateGateWayStatus() method in AluminumDeviceInfoServiceImpl.java was querying the entire em_aluminum_device_info table without filtering
3. Modified updateGateWayStatus() to add a QueryWrapper filter: .likeRight("device_category", "网关") to restrict automatic status updates only to gateway-class devices
4. Verified the change compiles successfully with JDK 8

## Related files
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/service/impl/AluminumDeviceInfoServiceImpl.java

## Notes
- None

## Task overview
Successfully fixed the leak aluminum device status issue: modified the updateGateWayStatus() method to only auto-update devices with device_category LIKE '网关%', preventing non-MQTT devices like '监测漏铝' from being falsely marked offline; the change compiled successfully with JDK 8.
