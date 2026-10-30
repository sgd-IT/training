---
title: "生产CDB上多个MySQL MCP服务的写权限分布"
usage_scenario:
    - "MCP写操作被权限拒绝需换服务时"
    - "对fireworks生产库执行增删改前"
    - "生产库大表备份需绕开GTID限制时"
keywords:
    - "MCP服务写权限开关"
    - "腾讯云CDB多库实例"
    - "ALLOW_DELETE_OPERATION"
    - "GTID禁止CTAS"
    - "mcp.json位置"
source: "auto"
---

腾讯云 CDB 实例 gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477 上同时承载 anwei_fire_remote_monitor、anwei_mon_fireworks、anwei_mon_fireworks_local、anwei_mon_fireworks_bak 等多个业务库。Qoder 在该实例上配置了多个 MySQL MCP 服务，写权限按服务环境变量独立开关：anwei-fire-remote-monitor-prod 关闭了 ALLOW_INSERT/ALLOW_DELETE（查询与 DDL 可用，写操作报 "operations are not allowed for schema"），而 anwei_mon_fireworks 服务开启了 ALLOW_INSERT/ALLOW_DELETE 且 MYSQL_DB=anwei_mon_fireworks，是 fireworks 业务库增删改的可用入口。服务清单与各服务的权限开关位于 C:\Users\Administrator\AppData\Roaming\Qoder\SharedClientCache\extension\local\mcp.json。该实例默认开启 GTID 一致性（ENFORCE_GTID_CONSISTENCY=1），CREATE TABLE ... SELECT 被禁止，备份需拆成 CREATE TABLE ... LIKE 加 INSERT ... SELECT 两步。
