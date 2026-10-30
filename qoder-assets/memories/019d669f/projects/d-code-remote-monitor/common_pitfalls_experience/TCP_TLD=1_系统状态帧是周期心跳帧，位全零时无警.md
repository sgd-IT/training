---
title: "TCP TLD=1 系统状态帧是周期心跳帧，位全零时无警"
usage_scenario:
    - "设计或实现TCP告警策略类时"
    - "解析TLD=1系统状态帧时"
    - "排查TCP链路误告警问题时"
keywords:
    - "心跳帧"
    - "系统状态帧"
    - "TLD=1"
    - "位域状态"
    - "保活"
source: "user"
---

Bug 类型：把 TCP（GBT26875/大华用传）TLD=1（type=0x01）系统状态帧当作告警帧解析。
根因：该帧是设备周期上报的心跳/保活帧，携带 alarm_fire/alarm_fault/alarm_normal_monitor 等状态位；心跳场景下位全为 0，仅表示"无警"，并不代表报警状态变化。
修复模式：仅在对应位=1 时触发 fire_alarm_event/fault_event/power_fault_event（现状 GbtDataReporter.handleSystemStatus 即如此）；位全零时只刷 alarm_* 属性并续保活（DeviceStateCheckHandler.updateDeviceLastTime），不产生告警记录。
可复用边界：适用于所有 GB/T 26875 用传设备 TCP 链路帧分类；告警策略类的 parse() 必须支持"一帧解析出 0 条告警"为正常路径，与 AEP smoke_state=1（正常态不告警）同模式。
