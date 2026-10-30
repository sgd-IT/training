---
title: "大屏今日值班误调不存在的duty-schedule/list接口"
usage_scenario:
    - "排查大屏值班人员显示异常或为空"
    - "核对前端请求路径与后端Controller清单"
    - "改造firewatch值班数据接入"
keywords:
    - "duty-schedule/list"
    - "current-duty接口"
    - "large-screen值班数据"
    - "接口静默失败"
    - "FireDutyScheduleController"
source: "auto"
---

Bug class：前端调用后端未注册的 REST 接口导致功能静默降级（anwei-fire-remote-monitor 值守域）
Root cause：large-screen/index.vue 的 loadData() 向 /eiot/firewatch/duty-schedule/list 请求值班数据，但 FireDutyScheduleController 只注册了 page / current-duty / list-by-date / list-by-date-range 等端点，无 /list；请求失败被前端兜底逻辑吞掉，页面继续用 mock/空数据渲染"今日值班"，看似正常实则数据来源错误。
Fix pattern：改调 /eiot/firewatch/duty-schedule/current-duty（该接口只查 fire_duty_schedule 当天排班），并做响应字段兼容（dutyUserName|userName）与状态映射（on_duty→ongoing）。
Reusable lesson：不要凭直觉拼前端请求路径，因为项目内 request 层对失败请求可能静默兜底而使错误不可见；应先 grep 后端 Controller 的 @GetMapping/@RequestMapping 清单核验路径存在再对接。适用于 firewatch 及 eiot 各子域的前后端联调与历史页面改造；不适用于已由全局拦截器统一抛错提示的请求链路。
