---
title: "移动端值班首页与PC端工作台API对齐任务"
usage_scenario:
    - "当需要将移动端功能与PC端保持API行为一致时"
    - "当后端接口HTTP方法变更影响多端一致性时"
    - "当移动前端状态判定逻辑需与后端SQL计算口径严格对齐时"
keywords:
    - "移动端API对齐"
    - "PC端API对齐"
    - "FireDutySchedule"
    - "HTTP方法统一"
    - "缺勤判定口径"
source: "auto"
---

## Task description
- Core requirement: align the mobile duty homepage (pages/duty/index.vue) with the PC dashboard (anwei-fire-remote-monitor-vue) for duty schedule APIs, ensuring identical paths, parameters, data structures, status mappings, and error handling.
- Task background: Mobile uses POST for check-in/check-out endpoints while PC uses PUT; backend has @PostMapping for check-out but @PutMapping for check-in; mobile's isAbsent logic differs from backend's SQL-based countDynamicAbsent; mobile lacks property name display and proper error state handling.

## Execution process
1. Backend FireDutyScheduleController.java: changed @PostMapping("/check-out") to @PutMapping("/check-out") to match PC's request.put usage and enable consistent HTTP method alignment.
2. Mobile utils/request.js: added export const put = (url, data, config) => request('PUT', url, data, config) to support PUT requests.
3. Mobile pages/duty/index.vue: replaced post() calls with put() for both check-in and check-out endpoints.
4. Mobile pages/duty/index.vue: updated isAbsent() logic to use string comparison (scheduleDate + endTime < nowStr) matching backend's CONCAT(schedule_date,' ',end_time) < NOW() semantics, removing the contradictory status==='absent' early-return.
5. Mobile pages/duty/index.vue: added propertyName display in the duty list card when present.
6. Mobile pages/duty/index.vue: refactored loadList() to avoid duplicate error toasts (request.js already handles this), set loadError flag on first-page failure, and show '加载失败，请下拉重试' instead of '暂无排班记录'.
7. Mobile pages/duty/index.vue: refactored loadStats() to use forEach(c => c.value = s[c.key] || 0) for reactive statCards update.
8. Mobile pages/duty/index.vue: removed dead onCardClick method and its template @click binding.

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\controller\admin\firewatch\FireDutyScheduleController.java
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\utils\request.js
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\duty\index.vue

## Notes
- The string-comparison logic for isAbsent was verified via node one-liner to match SQL '<' behavior (exactly at endTime is not absent).
- Cross-day night shifts (e.g., 20:00-08:00) use the same day's endTime for absent calculation, matching backend SQL; no change needed unless backend SQL changes.

## Task overview
Successfully aligned mobile and PC duty schedule APIs: backend check-out endpoint changed to @PutMapping; mobile added put() helper and updated all API calls; mobile isAbsent logic now matches backend SQL semantics; missing property name display added; error handling unified to prevent double toasts and improve UX; all changes verified and ready for deployment.
