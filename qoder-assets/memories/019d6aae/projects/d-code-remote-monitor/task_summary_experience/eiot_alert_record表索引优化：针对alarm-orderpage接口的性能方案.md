---
title: "eiot_alert_record表索引优化：针对/alarm-order/page接口的性能方案"
usage_scenario:
    - "为多租户告警表设计或复核复合索引列序"
    - "解释最左前缀原则与等值列前置的影响"
    - "用EXPLAIN执行计划判断索引使用效果"
    - "分析告警中心接口性能瓶颈"
keywords:
    - "eiot_alert_record"
    - "MySQL索引优化"
    - "alarm-order/page"
    - "复合索引"
    - "EXPLAIN"
    - "deleted列过滤"
    - "N+1查询"
source: "auto"
---

## Task description
- Core requirement: optimize the MySQL query performance for the `eiot_alert_record` table, specifically for the `/alarm-order/page` interface.
- Task background: the interface's 'all status' query pattern (`WHERE tenant_id = ? AND deleted = 0 ORDER BY alert_time DESC`) causes full table scan and filesort due to missing indexes.

## Execution process
1. Confirmed `doPageFromAlertRecord` uses MyBatis-Plus dynamic query building.
2. Analyzed query patterns and recommended three composite indexes:
   - `CREATE INDEX idx_tenant_alert_time ON eiot_alert_record (tenant_id, alert_time DESC)`
   - `CREATE INDEX idx_tenant_level_time ON eiot_alert_record (tenant_id, level, alert_time DESC)`
   - `CREATE INDEX idx_tenant_handle_status_time ON eiot_alert_record (tenant_id, handle_status, alert_time DESC)`
3. Provided `EXPLAIN` command examples and interpretation guide for verification.

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\firewatch\FireAlarmOrderServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\_sql\mysql\system.sql

## 相关记忆
- 本次优化的完整上下文和执行细节，请参阅新记忆：'移动端告警中心数据查询接口定位与索引优化'。

## Notes
- 【落地情况与最新结论（prod 实测）】三条建议索引仅 idx_tenant_alert_time 一条在线上创建，且实际列序为 (tenant_id, alert_time, deleted)，与本条记录的 (tenant_id, alert_time DESC) 不同；level/handle_status 两条未见创建。
- 【方案需迭代】(tenant_id, alert_time, deleted) 使 deleted=0 落到扫描后过滤（EXPLAIN type=ref、rows≈6852），应改为 (tenant_id, deleted, alert_time)；同时列表慢的主因是 doPageFromAlertRecord 转换循环的 N+1（fillDeviceType 三次查询 + getTenantName 一次，每页 20 条约 80 次额外查询），单靠索引无法解决。
- 【前置动作】复用本条方案前先 SHOW INDEX FROM eiot_alert_record 核对线上索引，勿把此处建议当作未实施状态直接执行。
