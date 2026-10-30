---
title: "DeviceStateCheckHandler.updateDeviceLastTime maintains device online state via TCP heartbeat"
usage_scenario:
    - "When implementing or debugging TCP device online/offline logic"
    - "When designing or verifying TCP heartbeat frame handling"
    - "When diagnosing device state synchronization issues in the rule engine"
keywords:
    - "DeviceStateCheckHandler"
    - "updateDeviceLastTime"
    - "TCP"
    - "heartbeat"
    - "device online"
source: "auto"
---

The `DeviceStateCheckHandler.updateDeviceLastTime` method is the core mechanism that keeps devices online in the TCP pipeline. It is triggered by any uplink message (e.g., property/event reports), and updates the device's last-active timestamp. Device offline detection is then performed by a timeout task using `keep_alive_time × 1.1`. This confirms the TLD=1 system status frame's primary role is heartbeat/keep-alive, not alarm generation.

## Notes
- Confirmed during this task: TLD=1 frame is used solely for keep-alive; bit=0 means no alarm, only property update and state refresh — it is not an alarm status frame.
