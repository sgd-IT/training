---
title: "UDP组件centerAddr配置迁移与初始化SQL更新"
usage_scenario:
    - "为UDP组件增改数据库JSON配置时"
    - "按记忆中的固定行id去更新eiot_component前核对真实id"
    - "排查设备拒收平台下发帧的地址问题"
keywords:
    - "centerAddr"
    - "eiot_component行id漂移"
    - "type=udp定位"
    - "初始化SQL不代表线上"
    - "确认帧源地址"
source: "auto"
---

## Task description
- Core requirement: add "centerAddr": "020000000001" to the UDP component's JSON configuration in the database
- Task background: UdpConfig.java already has centerAddr with default value "020000000001", but the user requires explicit JSON configuration; the UDP component config is stored in the eiot_component table's config column in the database; initialization SQL files need updating to include UDP component with centerAddr

## Execution process
1. Created migration SQL file _sql/mysql/20260824_udp_component_add_center_addr.sql to update existing UDP component records with centerAddr using JSON_SET
2. Modified system.sql (MySQL 8.x) to add UDP component record with ID=6 and centerAddr in JSON config
3. Modified system_mysql5.7.sql (MySQL 5.7) to add UDP component record with ID=6 and centerAddr in JSON config
4. Modified enjoy-iot.sql (PostgreSQL) to add UDP component record with ID=7 and centerAddr in JSON config, and updated sequence START from 7 to 8
5. Verified UDP component configuration through code analysis of UdpConfig.java and related files

## Related files
- /_sql/mysql/20260824_udp_component_add_center_addr.sql
- /_sql/mysql/system.sql
- /_sql/mysql/system_mysql5.7.sql
- /_sql/postgresql/enjoy-iot.sql

## Notes
- UdpConfig.java already has centerAddr default value "020000000001", so Jackson parsing will use this default if centerAddr is missing from JSON; explicit JSON configuration was added for visibility and consistency
- The JSON configuration includes port=48888, host="0.0.0.0", protocolMode="dahua", centerAddr="020000000001", and enableAutoRegister=true

## Task overview
Successfully completed UDP component centerAddr configuration: created migration SQL for existing databases and updated all three initialization SQL files (MySQL 8.x, MySQL 5.7, PostgreSQL) to include UDP component with centerAddr in JSON config; new deployments will automatically get the configured UDP component.

## Notes
- 仓库初始化 SQL 里写的 id（system.sql 的 id=6、enjoy-iot.sql 的 id=7）只代表基线脚本，不代表线上实际行 id：2026-10 连生产/测试库实测，生产库 type='udp' 的行是 id=7 且 status=0、config=NULL；测试库大华 UDP 组件行是 id=27、status=1、config 已配好但**仍缺 centerAddr**。定位目标行必须按 WHERE type='udp'（或 name）查，不能照搬记忆里的固定 id，否则更新会打空。
- "UdpConfig 有默认值所以 centerAddr 缺失无影响"这一结论对解析成立、对下发不成立：centerAddr 同时用作确认帧/控制帧的平台源地址，缺失时按全零处理，设备侧大概率拒收，故仍需在 config 显式补齐。
