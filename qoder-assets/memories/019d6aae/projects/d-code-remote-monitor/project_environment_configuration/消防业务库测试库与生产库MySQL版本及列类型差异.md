---
title: "消防业务库测试库与生产库MySQL版本及列类型差异"
usage_scenario:
    - "为告警表编写建索引或改列的迁移脚本"
    - "判断某写法在生产5.6下能否走索引"
    - "核对测试库结论能否直接推到生产库"
keywords:
    - "eiot_alert_record列类型"
    - "MySQL 5.6与5.7差异"
    - "bit(1)与tinyint"
    - "IS NOT NULL不走range"
    - "两库表规模对比"
source: "auto"
---

同一张 eiot_alert_record 在两套环境的服务端版本与列类型不一致，DDL/索引脚本不能照抄：

- 测试库（192.168.250.160:3306，MCP 实例 anwei_fire_remote_monitor_test）服务端 MySQL 5.7.38；deleted 为 bit(1) NOT NULL、read_flg 为 bit(1) NULL、level 为 varchar(255)、alarm_kind/tier 为 tinyint(4)、alert_time/tenant_id 为 bigint(20)。表约 34,717 行，数据 11.5MB、索引 3.0MB。
- 生产库（gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477，MCP 实例 anwei-fire-remote-monitor-prod）服务端 5.6.28-cdb2016-log；deleted 口径为整型且 deleted IS NULL 实测 0 行。表 17,415 行，tier 非空仅 492 行，tenant_id=174 占 15,825 行（91%）。
- 5.6 的 SQL 优化器对 `col IS NOT NULL` 不生成有界 range，EXPLAIN 退化为 type=index（全索引扫描）；只有写成闭集区间（如 BETWEEN 1 AND 4）才得到 type=range。生产库无 EXPLAIN ANALYZE，也无 invisible index。
- 两库 eiot_alert_record 原有索引完全相同：PRIMARY(id) 与 idx_tenant_alert_time(tenant_id, alert_time, deleted)。
