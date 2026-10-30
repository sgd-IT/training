---
title: "值班交接班真表是fire_handover_record而非DO指向的fire_duty_record"
usage_scenario:
    - "实现或排查值班交接班统计取数"
    - "遇到fire_duty_record表不存在的500错误"
    - "判断firewatch值班域哪张表可安全复用"
keywords:
    - "fire_duty_record"
    - "fire_handover_record"
    - "交接班表"
    - "坏表引用"
    - "duty-stats 500"
source: "auto"
---

消防值守（firewatch）值班域存在代码与数据库迁移不同步的结构性事实（本轮 duty-stats 接口 500 排查中证实）：

- FireDutyRecordDO / FireDutyRecordMapper 映射的表 `fire_duty_record` 在开发库 anwei_fire_remote_monitor 中**根本不存在**（SHOW TABLES 无此表），任何走该 Mapper 的查询都会抛 "Table doesn't exist" → 接口 500。FireDutyRecordServiceImpl 及前端 duty-record/index.vue 是这套坏引用，但对应路由已在 remaining.ts 注释、无用户入口，属埋着的雷。
- 交接班数据的**真实表是 `fire_handover_record`**（有数据；关键列 handover_time、tenant_id、deleted、outgoing_person/incoming_person，**无 property_id**）。按物业下钻只能到租户级近似口径。
- 另有 `fire_duty_log` 表存在于库上但当前无任何代码引用。
- 正确做法：需要交接班计数时新建/使用 FireHandoverRecordMapper.countInRange(handover_time 左闭右开)，不要复用指向 fire_duty_record 的 FireDutyRecordMapper。

适用边界：涉及 firewatch 值班/交接班取数时。不适用：排班表 fire_duty_schedule（真实存在且有唯一键 tenant_id+property_id+schedule_date+shift_type）。

## Notes
- 【2026-10 最新状态修正】上述"fire_duty_record 不存在、路由被注释"仅对当时的开发库成立。后续任务已通过 MySQL MCP 在开发库 anwei_fire_remote_monitor 执行 DDL **新建了 fire_duty_record 表**（字段与 FireDutyRecordDO 一一对应，索引 tenant_id/property_id/record_time/schedule_id/record_type，字符集对齐 fire_duty_schedule），并把 remaining.ts 中 FireWatchDutyRecord 路由**取消注释放开**（hidden: true，作值班管理页跳转入口），duty/index.vue 的"正在开发中"兜底提示同步删除。生产库当时核查仍无此表，上线前需另行执行同一 DDL。判断该表可用性时必须先确认所连库实例与时间点。
