---
title: "UDP/TCP/AEP告警链路实时提示机制全链路分析报告"
usage_scenario:
    - "审计UDP/TCP/AEP三种设备接入方式的实时告警能力差异"
    - "诊断某类告警未在Web或移动端触发弹窗的原因"
    - "为新增告警来源规划实时推送能力时参考现状矩阵"
keywords:
    - "UDP"
    - "TCP"
    - "AEP"
    - "alert chain"
    - "real-time push"
    - "WebSocket"
    - "polling"
source: "auto"
---

## Task description
- Core requirement: analyze the implementation logic of UDP, TCP, and AEP three alert chains to confirm whether they provide consistent real-time prompt mechanisms (e.g., pop-up, sound, or message notifications) for Web and mobile ends, or are handled silently (background logging only).
- Task background: the system supports multiple IoT alarm ingestion channels; understanding the real-time capability of each is critical for operational reliability and user experience.

## Execution process
1. Backend codebase exploration: traced data flow from UdpVerticle → MQ → rule engine for UDP/TCP; confirmed AepCallbackServiceImpl uses dual-path (MQ + AlarmRecordService) for AEP.
2. WebSocket analysis: confirmed monitoring-center listens to 'alarm-push', duty-screen listens to 'firewatch-alert-new'; neither receives alerts from TCP/UDP rule-engine path.
3. Frontend polling: verified duty-screen polls getRecentUnprocessedAlerts every 10s, monitoring-center polls checkNewAlarms every 30s — this is the only real-time mechanism for TCP/UDP alerts.
4. Mobile analysis: confirmed uni-app has no WebSocket, no push, no sound, no vibration — purely manual pull-to-refresh.
5. Final matrix: compiled a complete behavior matrix comparing all three chains across all dimensions (backend WS, Web duty-screen, Web monitoring-center, mobile, external channels).

## Related files
- /module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/iot/AlertMessageEventListener.java
- /src/views/eiot/firewatch/duty-screen/index.vue
- /src/views/eiot/firewatch/monitoring-center/index.vue
- /pages/index/index.vue

## Notes
- Critical finding: Only AEP has true real-time push; UDP/TCP are silent for frontend and rely solely on polling.
- Key defect: 'playAlarmSound()' is dead code; current system uses only TTS speech, not audio tones.
- Mobile is completely silent for all three chains; only SMS/VMS via backend rule-engine can reach users offline.

## Task overview
Successfully completed a full-stack analysis of UDP/TCP/AEP alert chains: identified that only AEP has WebSocket-based real-time push; UDP/TCP rely on polling (≤10s delay); mobile is entirely silent; documented the architecture gap and proposed immediate fixes.

## Notes
- "仅AEP具备真实时推送、其余链路静默只靠轮询"的结论已过时：规则引擎 AlertMessageEventListener 与摄像头 DeviceAlertConfigServiceImpl.reportCameraAlert 现在均在落库成功后直接调用 AlarmRealtimeNotifyService.notifyAlarmSaved 发出 firewatch-alert-new 秒级推送（2026-09-30 热成像接口测试实测证实：调 /app-api/open/camera-alert/report 后同进程在线会话即收到推送）。排查弹窗问题时不得再假设"只有AEP会实时弹窗"，须把所有 notifyAlarmSaved 调用方纳入范围。
- 本报告的移动端"完全静默"结论仅适用于当时的 uni-app 首页实现快照，摄像头链路弹窗验证均在 PC 端 duty-screen/monitoring-center 完成，不涉及移动端。
- 最新补充（今日警情实时化方案核实轮次）：duty-screen / monitoring-center / alarm-screen 的「今日警情」卡片数据来自 /eiot/firewatch/dashboard/statistics（后端 buildTodayAlarm 用 countToday* 实时 SQL 计数），两页均为 30 秒整页轮询刷新；而报警记录管理页 alarm-record/index.vue 的统计卡片走 alarm-order/statistics，是全量聚合口径、不是"今日"，做"今日警情数字实时变动"类需求时不要连带改该页面。
