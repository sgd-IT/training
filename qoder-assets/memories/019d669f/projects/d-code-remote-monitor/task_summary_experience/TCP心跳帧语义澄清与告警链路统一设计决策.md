---
title: "TCP心跳帧语义澄清与告警链路统一设计决策"
usage_scenario:
    - "实现或修正TCP告警解析逻辑时"
    - "设计跨协议统一告警落库时"
    - "解决AEP双路径告警重复时"
keywords:
    - "TCP心跳帧"
    - "TLD=1"
    - "raw_payload"
    - "告警链路统一"
    - "AEP builtin rules"
    - "dual-chain"
source: "auto"
---

## Task description
- Core requirement: clarify the semantic role of TCP TLD=1 system status frame and unify the alarm pipeline across UDP/TCP/AEP protocols
- Task background: User stated "TCP这个属于心跳无报警"; agent verified `DeviceStateCheckHandler.updateDeviceLastTime` uses this frame for device keep-alive; matrix classification needed correction; `raw_payload` column decision made; AEP dual-path dedup mechanism confirmed as in-memory `ConcurrentHashMap`; configuration vs builtin rule unification strategy settled

## Execution process
1. Verified TLD=1 frame is used by `DeviceStateCheckHandler.updateDeviceLastTime` to maintain device online state, confirming its primary role is heartbeat/keep-alive, not alarm generation
2. Corrected matrix row: reclassified T2 from "alarm status frame" to "periodic heartbeat/keep-alive frame" — bit=0 means no alarm, only property update and state refresh
3. Decided on `eiot_alert_record.raw_payload` TEXT column to store original payload: UDP/TCP store full hex frame, AEP stores raw JSON body
4. Confirmed `AlarmRecordService` uses in-memory `ConcurrentHashMap` (5-minute window) for deduplication, not Redis
5. Defined unified alarm pipeline: all protocols use same `eiot_alert_record` table; configuration is the sole rule entry point; AEP builtin rules remain active and operate in parallel with DeviceAlertChecker without mutual exclusion — dual-chain independence confirmed.

## Related files
- anwei-fire-remote-monitor\module-eiot\module-eiot-core\iot-rule-engine\src\main\java\com\enjoyiot\eiot\ruleengine\handler\sys\DeviceStateCheckHandler.java
- anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AlarmRecordService.java

## Notes
- None

## Task overview
Successfully clarified TCP TLD=1 frame semantics and established unified design decisions for the alarm pipeline: heartbeat frame role confirmed and matrix updated; `raw_payload` column selected for unified payload storage; AEP dedup implementation verified; configuration-first unification strategy agreed upon for long-term maintainability. AEP builtin rules remain active and dual-chain design unchanged.
