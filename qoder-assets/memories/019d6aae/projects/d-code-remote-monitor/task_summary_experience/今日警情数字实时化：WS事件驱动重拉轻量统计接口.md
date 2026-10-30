---
title: "今日警情数字实时化：WS事件驱动重拉轻量统计接口"
usage_scenario:
    - "When designing real-time UI updates driven by WebSocket events"
    - "When optimizing frontend performance under high-frequency event bursts"
    - "When extending backend APIs to support tenant-scoped queries"
keywords:
    - "today-alarm"
    - "WebSocket"
    - "real-time refresh"
    - "tenantId"
    - "debounce"
source: "auto"
---

## Task description
- Core requirement: make the "Today's Alarm Count" number on duty-screen and monitoring-center update in real time when a system alert is triggered, instead of waiting for the 30-second polling cycle
- Task background: both pages already receive WebSocket firewatch-alert-new events; the existing today-alarm interface returns aggregated stats but lacks tenantId support for admin tenants; current implementation uses full-page loadData() which is heavy under alarm storms

## Execution process
1. Backend FireDashboardController.java: add optional tenantId parameter to /dashboard/today-alarm GET endpoint
2. Backend FireDashboardService.java: declare overloaded getTodayAlarmStatistics(Long tenantId) method, keeping the original no-arg method for AI module compatibility
3. Backend FireDashboardServiceImpl.java: implement both methods using executeWithTenantAware, ensuring non-admin users' tenantId is ignored
4. Frontend firewatch.api.ts: extend getTodayAlarm() to accept optional tenantId and pass it as query param
5. Frontend duty-screen/index.vue: add refreshTodayAlarm() with 800ms debounce, call it on WS firewatch-alert-new receipt and on successful onHandleNow(), clean up timer on unmount
6. Frontend monitoring-center/index.vue: same pattern — add refresh function, trigger on WS event and submitConfirmFire/submitFalseAlarm success, cleanup on unmount
7. Verification: mvn compile passed; ESLint on all 3 frontend files passed; git diff confirmed targeted changes only

## Related files
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/admin/firewatch/FireDashboardController.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireDashboardService.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireDashboardServiceImpl.java
- anwei-fire-remote-monitor-vue/src/api/eiot/firewatch/firewatch.api.ts
- anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty-screen/index.vue
- anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/monitoring-center/index.vue

## Notes
- The decision to use WS-driven re-pull over SSE or optimistic frontend +1 was justified by avoiding redundant infrastructure and preventing incorrect calculations (e.g., multi-tenant double-count, pendingCount/handleRate dependency, tenant-switch context loss)
- TenantId parameter ensures super-admins see accurate numbers after switching tenants
- 800ms debounce merges burst alerts into one request, reducing load during alarm storms
- Lightweight today-alarm interface (4 SQL aggregates) avoids triggering full-page loadData() (7 blocks)

## Task overview
Successfully implemented real-time today-alarm count updates: backend added tenantId support to the today-alarm API; frontend added debounce-triggered lightweight stat re-pull on WebSocket alert events and disposition actions in duty-screen and monitoring-center; all changes compiled and linted successfully.
