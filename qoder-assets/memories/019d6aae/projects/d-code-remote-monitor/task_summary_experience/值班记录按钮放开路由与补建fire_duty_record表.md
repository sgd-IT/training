---
title: "值班记录按钮放开路由与补建fire_duty_record表"
usage_scenario:
    - "排查消防值守页面按钮跳转失效或死路由问题"
    - "放开被注释的静态路由前核对表与接口就绪度"
    - "处理fire_duty_record相关的值班记录功能"
keywords:
    - "值班记录按钮"
    - "FireWatchDutyRecord路由"
    - "fire_duty_record建表"
    - "死按钮排查"
    - "hidden静态路由"
source: "auto"
---

## Task description
- Core requirement: 用户先问"值班记录按钮是否有用"，经交互审查确认是死按钮后要求"放出来"，即让值班管理页（duty/index.vue）的"值班记录"按钮成为有效跳转入口。
- Task background: 按钮走 router.push({name:'FireWatchDutyRecord'})，但 remaining.ts 中该路由整段被注释、permission.ts 的 FIREWATCH_EXTRA_MENUS 也全为空注释，点击 100% 落入 catch 弹"正在开发中"；页面 duty-record/index.vue 功能完整（查询/新增/编辑/删除/统计/导出），后端 FireDutyRecordController 存在且权限串复用 eiot:firewatch:duty:*，但 fire_duty_record 表在库里不存在（存量缺陷）。

## Execution process
1. 判定死按钮链路：确认 remaining.ts 路由注释 + FIREWATCH_EXTRA_MENUS 空注释 + 静态注册是唯一途径，router.push({name}) 必报 No match 进 catch。
2. 职能对比结论：duty 页是排班计划+考勤（谁上班），duty-record 页是事件日志（上班干了啥），二者经 scheduleId 关联不重复；真正重叠在记录类型 patrol/alarm/handover 与巡检、火警处置、换班模块并行两套台账。
3. 用 MCP mysql_query 核实开发库 anwei_fire_remote_monitor SHOW TABLES 无 fire_duty_record，仅 fire_duty_log/fire_duty_schedule/fire_handover_record。
4. 参照 fire_duty_schedule 的 SHOW CREATE TABLE 模板生成 DDL（列对齐 FireDutyRecordDO，索引 tenant_id/property_id/record_time/schedule_id/record_type，utf8mb4_unicode_ci），MCP 执行建表成功，COUNT(*) 验证通过。
5. remaining.ts 取消 FireWatchDutyRecord 路由注释并保持 hidden:true（只做跳转入口不占侧边菜单）。
6. duty/index.vue 的 handleViewRecords 删掉 ElMessage.info('值班记录页面正在开发中')，改为 router.push(...).catch(() => {}) 静默兜底。
7. 前后端参数核对：page 的 recordTimeStart/recordTimeEnd 与 FireDutyRecordPageReqVO 同名一致；statistics 的 startTime/endTime（yyyy-MM-dd HH:mm:ss）与 @DateTimeFormat 一致。
8. 生产库 MCP 查询返回空——fire_duty_record 在生产同样不存在，上线前必须执行同一 DDL，已明确告知待用户授权。

## Related files
- /anwei-fire-remote-monitor-vue/src/router/modules/remaining.ts
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty/index.vue
- 开发库 anwei_fire_remote_monitor：新建 fire_duty_record 表（DDL via MCP）

## Notes
- 静态路由（remaining.ts）在 pinia store 初始化时 concat 装配，改完必须 F5 硬刷新才生效，HMR 不会重新注入。
- "放出路由"这类一行改动交付前必须先核对该页面的数据依赖（表/接口）是否就绪，否则交付的是必然 500 的坏功能；本例靠代码注释（FireDataCenterServiceImpl 写明"该表在数据库并不存在"）+ MCP SHOW TABLES 双重证实。
- 生产建表属生产变更，即便 MCP 支持 DDL 也要先征询用户再执行。

## Task overview
已完成（开发环境）：路由放开 + 误导文案清除 + 开发库补建 fire_duty_record，按钮从死按钮变为有效跳转；参数链路与权限串均核对通过。遗留：生产库仍缺该表，需用户点头后执行同一 DDL 方可上线。
