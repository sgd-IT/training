---
title: "MCP多MySQL实例配置方式"
usage_scenario:
    - "增删或修改mcp.json中的MySQL实例连接参数时"
    - "区分测试库与生产库MCP实例及权限开关时"
    - "生产库实例迁移后核对配置清单时"
keywords:
    - "mcp.json"
    - "MySQL实例"
    - "多数据库"
    - "MCP配置"
    - "anwei-fire-remote-monitor-prod"
    - "gz-cdb-dnqlqqwx"
    - "ALLOW_DDL_OPERATION"
    - "只读实例"
source: "auto"
---

项目可通过在 mcp.json 中配置多个 MySQL MCP Server 实例来支持多数据库查询，每个实例需使用唯一名称，并分别配置对应环境的连接参数（HOST、PORT、USER、PASS、DB）。只读实例的表达方式：四个 ALLOW_*_OPERATION 显式设 "false"（或不设置），服务端仅在严格等于 "true" 时放行写操作。

实例清单（两份 mcp.json 需同步修改：用户级 .qoder\mcp.json 与实际生效的 SharedClientCache\extension\local\mcp.json，改后需重载 MCP 才生效）：
- anwei_fire_remote_monitor_test：LAN 测试库 192.168.250.160:3306 / anwei_fire_remote_monitor，读写与 DDL 按需放行。
- anwei-fire-remote-monitor-prod：生产库。2026-10-19 起指向腾讯云 gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477 / root / anwei_fire_remote_monitor（实测 111 张表，eiot_alert_record 最新告警当天，数据活跃）；INSERT/UPDATE/DELETE=false 仅查询，ALLOW_DDL_OPERATION 仍为 true。旧地址 gz-cdb-nmt6uclj.sql.tencentcdb.com:26671 上的同名库数据停在 2026-09-02，已弃用不再作为 prod 连接目标。
- anwei_mon_fireworks：同实例 gz-cdb-dnqlqqwx:60477 下的另一默认库 anwei_mon_fireworks，当前 disabled=true。
