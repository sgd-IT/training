---
title: "消防业务库MySQL MCP实例连接现状与服务名漂移判定"
usage_scenario:
    - "调用mysql_query前确认实例与环境"
    - "生产库只读查询兜底"
    - "排查MCP连接与SSL报错"
keywords:
    - "mysql_query实例"
    - "anwei-fire-remote-monitor-prod"
    - "生产库60477"
    - "SSL DISABLED"
    - "MCP实例确认"
source: "user"
---

MCP 提供多个指向消防业务库的 mysql_query 实例，库名相同不代表实例相同，调用前必须先确认用的是哪个实例：

- anwei_fire_remote_monitor_test：测试库（192.168.250.160:3306，库名 anwei_fire_remote_monitor），SELECT/INSERT/UPDATE/DELETE/DDL 已放行，本仓库 eiot_* / fire_* / system_* / infra_* 业务表可直查。SELECT @@hostname, @@port, DATABASE() 可核对落到 WINDOWS-3D9R3V5:3306（LAN 库）。
- anwei-fire-remote-monitor-prod：生产库。2026-10-19 起改连腾讯云 gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477 的 anwei_fire_remote_monitor（111 张表，告警当日活跃）；旧地址 gz-cdb-nmt6uclj:26671 数据停在 2026-09-02 已弃用。配置为 UPDATE/INSERT/DELETE=false、ALLOW_DDL_OPERATION=true，实际只做查询。
- anwei-iot-db-prod：旧生产主机 gz-cdb-nmt6uclj:26671，默认库 anwei_iot_db，已过时。

服务名以会话实测为准：调用前先列 mcps 目录 + 读 SERVER_METADATA.json / tools/*.json 确认 server 名，再用 SELECT DATABASE() 确认落到哪套环境。

配置落点：实际生效的连接参数在 C:\Users\Administrator\AppData\Roaming\Qoder\SharedClientCache\extension\local\mcp.json（项目级 SharedClientCache\projects\<workspace>\mcps\<server>\ 下另有 tools/SERVER_METADATA）；用户级 c:\Users\Administrator\.qoder\mcp.json 需同步修改，重载 MCP 后生效。

权限判定逻辑（服务端 @benborla29/mcp-server-mysql）：写操作需 ALLOW_INSERT/UPDATE/DELETE/DDL_OPERATION 显式为字符串 "true"；DDL 判定为 SCHEMA_DDL_PERMISSIONS[库名] 优先、否则回退 ALLOW_DDL_OPERATION。写操作直接落库、无事务回滚保护：写前 SELECT 确认、写后 SELECT 验证。

测试库 system_users 现状口径（2026-10-16 实测）：tenant_id=1（安为科技值守中心）下 status=0 用户 8 个，超管 admin 在 dept_id=103 研发部门；tenant 165 仅 1 人、166 有 6 人、167 为 0 人、168 仅 1 人——造排班演示人员必须落在目标物业所属的 tenant_id 下才会在下拉出现。user_type 取值 1=Web、2=App、3=两端皆可，造可登录账号一般给 3。

另外：MCP 服务可能会话中途掉线；掉线时按行为约束改用 mysql.exe CLI 兜底（只读优先），之后仍回 MCP 校验。本机 mysql.exe 8.0.46 连 Tencent CDB 需加 --ssl-mode=DISABLED，否则报 SSL routines::unsupported protocol。
