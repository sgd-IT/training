---
title: "AEP告警接入与多模块配置优化的跨仓库原子化提交"
usage_scenario:
    - "When integrating AEP平台回调接口并同步更新后端配置与前端实时推送时"
    - "当需批量清理项目中冗余版权头并确保不破坏构建与运行时行为时"
    - "排查多仓库提交后配置不一致或功能缺失问题"
keywords:
    - "AEP告警"
    - "application.yaml配置"
    - "版权头清理"
    - "跨仓库提交"
    - "Angular规范"
source: "auto"
---

## Task description
- Core requirement: execute coordinated Git commits across backend and frontend repositories following Angular specification, integrating AEP alarm callbacks, extending alarm types, fixing rule-engine logic, and cleaning up redundant license headers
- Task background: user requested '提交git'; assistant discovered complex multi-repo changes including AEP callback handling, alarm service enhancement, rule-engine repair, and bulk copyright header deletion; simultaneously uncovered the need to surgically insert config lines into application.yaml without submitting environment changes

## Execution process
1. Backend: fix(tcp) — corrected iot-component-tcp-remote-monitor/pom.xml artifactId from iot-component-tcp-gbt26875-ff to match directory name
2. Backend: feat(aep) — added /admin-api/ai/data/** and /admin-api/eiot/aep/** to application.yaml permit-all_urls, and /admin-api/eiot/aep/** and /infra/ws to ignore-urls
3. Backend: feat(alarm) — extended AlarmMessage, AlarmRecordService, FireAlarmOrderServiceImpl, FireDashboardServiceImpl to support info alarm type
4. Backend: fix(rule-engine) — modified DeviceStateCheckHandler to always update device state for TYPE_STATE messages
5. Backend: chore(license) — deleted Apache License 2.0 headers from 465 Java files
6. Vue: feat(firewatch) — enabled WebSocket real-time alarm push in useAlarmPopup.ts, duty-screen/index.vue, monitoring-center/index.vue

## Related files
- module-eiot/iot-components/iot-component-tcp-remote-monitor/pom.xml
- server/src/main/resources/application.yaml
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmMessage.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmRecordService.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireAlarmOrderServiceImpl.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireDashboardServiceImpl.java
- module-eiot/module-eiot-core/iot-rule-engine/src/main/java/com/enjoyiot/eiot/ruleengine/handler/sys/DeviceStateCheckHandler.java
- src/components/AlarmPopup/useAlarmPopup.ts
- src/views/eiot/firewatch/duty-screen/index.vue
- src/views/eiot/firewatch/monitoring-center/index.vue

## Notes
- Critical insight: To avoid submitting profiles.active: dev, application.yaml was first restored to HEAD, then only the 4 URL lines were inserted via SearchReplace before staging — this surgical edit pattern is reusable for any config file with mixed business/environment changes
- Avoided pitfalls: excluded deploy/docker-compose.yml, WebProperties.java, server/pom.xml, .env.local, and the profiles.active line itself

## Task overview
Successfully delivered a coordinated 6-commit feature release across backend and frontend repositories: backend implemented AEP callback integration with precise URL whitelisting, extended alarm type support, fixed rule-engine state updates, and removed 465 redundant license headers; frontend enabled real-time WebSocket alarm push in duty-screen and monitoring-center — all following Angular commit conventions and verified via final git status checks.
