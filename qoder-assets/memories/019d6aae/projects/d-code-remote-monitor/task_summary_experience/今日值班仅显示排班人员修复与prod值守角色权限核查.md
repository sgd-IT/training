---
title: "今日值班仅显示排班人员修复与prod值守角色权限核查"
usage_scenario:
    - "修改大屏今日值班展示或数据来源"
    - "核查prod值守角色权限与数据可见性"
    - "排查排班数据租户归属异常"
    - "验证物业归属过滤是否生效"
keywords:
    - "今日值班"
    - "current-duty接口"
    - "duty_person零权限"
    - "排班租户归属"
    - "大屏角色可见性"
source: "auto"
---

## Task description
- 核心需求：①"今日值班只显示值班排班的人"；②核查 prod 环境值守/值班类角色能否看到当前值班人员、是否只见安为科技公司数据、能否看全部租户或过滤到对应租户。
- 任务背景：PC 三块大屏（duty-screen / monitoring-center / large-screen）的今日值班数据来源不同；生产库角色配置与 test 库存在差异，需逐层取证。

## Execution process
1. 前端数据源盘点：duty-screen 与 monitoring-center 均优先取 dashboard/statistics 响应的 currentDutyList，兜底调 /eiot/firewatch/duty-schedule/current-duty；large-screen/index.vue 的 loadData() 却调用后端不存在的 /eiot/firewatch/duty-schedule/list，改为 /current-duty，并做字段兼容（dutyUserName|userName）与状态映射（on_duty→ongoing）。
2. 后端口径确认：FireDashboardServiceImpl.buildCurrentDutyList() 已重构为按物业归属过滤（property_id），不再依赖 tenant_id；系统租户(1)走 executeIgnoreWithResult 查全部——"只查排班表 + 按物业归属过滤"逻辑已落地，确保所有租户的排班都能被正确展示。
3. prod MCP 查 system_role：无 fire_watcher（仅 test 库有）；monitor_role(id=162, tenant_id=1) 有 eiot:firewatch:dashboard:query；duty_person(id=169, 碧桂园租户174) 在 system_role_menu 无任何授权，无法访问大屏。
4. prod 排班归属核查：fire_duty_schedule 中"安为科技大厦"(property_id=1009) 记录 tenant_id=0（脏数据，fire_property 里该物业实际 tenant_id=1），另有 tenant_id=178 的正常排班；安为相关租户 165/166/167 均只有 tenant_admin 且无排班数据。
5. 结论交付：系统租户账号可见全部租户排班、企业账号仅本租户；给出给 duty_person 补 3001/3101 菜单授权的 SQL 模板待用户确认，未执行写库。

## Related files
- anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/large-screen/index.vue

## Notes
- large-screen 是独立于 duty-screen/monitoring-center 的第三块屏，改造值班展示时三屏都要核对数据源。
- 排查接口是否存在应以后端 Controller 注解清单为准，前端路径不在清单内即为无效调用（可能被静默兜底）。
- 角色名跨环境不一致（test 有 fire_watcher，prod 是 duty_person/monitor_role），权限核查必须先确认所连库实例。

## Task overview
部分完成：large-screen 今日值班改接 /current-duty 已落盘（未提交 git），确保三屏均只展示排班表人员；prod 角色可见性核查完成并输出结论与待确认授权 SQL——duty_person 零权限是当前生产缺口，安为科技大厦排班 tenant_id=0 脏数据待治理。
