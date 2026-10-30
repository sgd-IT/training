---
title: "Qinghai Qiaotou Aluminum Power Co., Ltd. (377) project configuration"
usage_scenario:
    - "When diagnosing alarms for customer 377"
    - "When verifying device mapping or signal point configuration for Qinghai Qiaotou Aluminum Power Co., Ltd."
    - "When configuring or troubleshooting non-casting period leak monitoring"
keywords:
    - "Qinghai Qiaotou Aluminum Power"
    - "377"
    - "1# Casting Unit"
    - "non-casting leak detection"
    - "QHQTLD_*"
source: "auto"
---

Qinghai Qiaotou Aluminum Power Co., Ltd. (customer_id=377) is configured in the platform with the following verified facts:
- Single casting unit: well id=118 "1# Casting Unit", node_id=08b96d0fd4c64890b556ffb80f7dd030
- Device identifier: device_num=qhld001, model=sjzz01, PLC point prefix=QHQTLD_*
- No Jiago sound-leakage terminals deployed (no entries in em_aluminum_device_info, zero records in mon_aluminum_leakage_alarm_record)
- Alarm system uses mon_alarm_information table; 56 alarms recorded 2026-09-18 to 2026-09-19, including 'non-casting period leak detection' for furnace mouth level and temperature
