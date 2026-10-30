---
title: "fire-remote-monitor database infrastructure and MCP configuration"
usage_scenario:
    - "When diagnosing database performance bottlenecks or planning capacity expansion"
    - "Before choosing which database instance to use for maintenance tasks"
keywords:
    - "MySQL 5.6"
    - "MySQL 5.7"
    - "infra_api_access_log"
    - "MCP configuration"
    - "Tencent Cloud CDB"
source: "auto"
---

The fire-remote-monitor project uses two MySQL database instances:
- Production: Tencent Cloud CDB `gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477`, MySQL 5.6.28-cdb2016-log, database `anwei_fire_remote_monitor` (112 tables, 135.5 MB), MCP service `anwei-fire-remote-monitor-prod` with `ALLOW_DDL_OPERATION=true` but INSERT/UPDATE/DELETE disabled.
- Test: LAN `192.168.250.160:3306`, MySQL 5.7.38, same database name (121 tables, 555.8 MB), MCP service `anwei_fire_remote_monitor_test` with full DDL/DML/READ permissions.
Key structural fact: `infra_api_access_log` dominates both databases — 114.2 MB / 279K rows in production, 464.6 MB / 1M rows in test — and its growth must be managed carefully due to MySQL 5.6's lack of instant DDL.
