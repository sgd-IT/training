---
title: "消防业务库MySQL MCP实例现状：anwei_fire_remote_monitor_test已生效可直查"
usage_scenario:
    - "通过MCP查询消防业务库表结构或数据前"
    - "核对线上生产库与测试库表字段差异时"
    - "判断某SQL迁移脚本是否已在生产执行时"
    - "确认UDP设备接入在生产是否已启用"
keywords:
    - "mysql_query"
    - "anwei-fire-remote-monitor-prod"
    - "mcps目录可用性核查"
    - "not found重试即可"
    - "DDL已放行"
    - "information_schema列探查"
    - "两库列类型不一致"
    - "ssl-mode DISABLED"
    - "gz-cdb-dnqlqqwx"
    - "全限定表名"
source: "auto"
---

## 当前状态（2026-09-28 实测，2026-10-08/09/16/19/29 多次更新）
- 生效配置：实际配置文件为 `SharedClientCache\extension\local\mcp.json`（与用户级 `.qoder\mcp.json` 两份需同步），测试实例 `anwei_fire_remote_monitor_test`（npx @benborla29/mcp-server-mysql；192.168.250.160:3306，root，库 anwei_fire_remote_monitor，工具 `mysql_query`）。
- 该实例已验证可完成：表行数统计、分布聚合、`information_schema.columns` 列结构查看、全表清单拉取、**ALTER TABLE 建索引（DDL）**；本仓库 eiot_* / fire_* / system_* / infra_* 业务表均可直接经 MCP 查询，不得再沿用"业务库不可访问需绕道 DDL 文件"的旧结论。
- 【2026-10-29 订正：DDL 已放行】此前【2026-10-08】记录的"MCP 拒绝 DDL（SCHEMA_DDL_PERMISSIONS 报错）"**已不成立**：本轮在同一测试实例上直接执行 `ALTER TABLE ... ADD INDEX` 返回 "DDL operation successful"（耗时 2.58s）。因此数据库操作一律首选 MCP，**不得凭旧记忆预设 DDL 受限而提前绕 mysql.exe CLI**——本轮绕 CLI 连续踩参数截断与 SQL 文件 UTF-16 损坏，白耗多轮。原兜底通道（本地 `D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe` + `--execute=source <绝对路径.sql>` + `$env:MYSQL_PWD`）仅在 MCP 确认不可用时使用。
- 【2026-10-29 订正："not found MCPHost severName" 必须重试】该报错是**瞬时**的：本轮调用 test 实例首次报 not found，遂判定实例缺席并改用 CLI 查了 6+ 轮；之后用**完全相同的 server 名重试即成功**。故正确动作是：先重试 1~2 次（可换一条极简 SELECT 探活），仍失败才降级 CLI；不得一次失败就宣布实例不可用。
- 【2026-10-09：MCP 可用性会随会话波动】某次会话中 `dir ...\mcps` 列出的可用 server 没有 MySQL 实例。故每次动用数据库前先列 mcps 目录确认可用性；MySQL MCP 缺席时不要臆测其在线，改用代码侧证据替代查库。注意 user_behavior 记忆「数据库操作优先使用 MCP」里写的实例名 hc-standard-test 已过时，以本条为准。
- 【2026-10-16：实例名漂移与默认库陷阱】某会话 mcps 目录列出的消防库实例名为 `anwei-iot-db-prod`（非 test/prod 老名），默认库是 anwei_iot_db——直接查业务表报 Table doesn't exist，必须写全限定名 `anwei_fire_remote_monitor.<table>`；先用 `information_schema.tables WHERE table_name='xxx'` 反查 table_schema 定位库。
- 【2026-10-19：生产库实例迁移，判定环境的最快判据更新】生产库连接已从 gz-cdb-nmt6uclj:26671 迁至 **gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477**（MCP 实例 anwei-fire-remote-monitor-prod 已同步改配，两份 mcp.json 均更新）。新实例上 anwei_fire_remote_monitor 有 111 张表且 eiot_alert_record 当天仍在写入；旧实例同名库数据停在 2026-09-02。**当前活跃生产库应查新实例**，不要再在旧实例找当天数据。另：本机 mysql.exe 直连腾讯云 CDB 必须加 `--ssl-mode=DISABLED`，否则报 SSL routines::unsupported protocol；MCP 的 node 驱动不受影响。
- 【2026-10-29 补充：两库列类型不一致，DDL 脚本不可照抄】同一张 eiot_alert_record：测试库服务端 5.7.38，`deleted`/`read_flg` 为 bit(1)、`level` varchar(255)；生产库 5.6.28-cdb，口径为 tinyint/int。跨库建索引或改列前必须先 `information_schema.columns` 逐列核对，不能拿一库的 DDL 直接推另一库。

## 历史教训（保留作方法参考）
- 2026-09 曾出现生效实例为 `tgr-gz-vue`（另一项目库，仅 tgr-gz-vue / tgr-gz-vue_copy 两个 schema），查 `eiot_device_info` 报 Table doesn't exist。
- 正确做法不变：调用前先列 mcps 目录 + 读 SERVER_METADATA.json / tools\*.json 确认 server 名与工具 schema，用 SELECT DATABASE() 判定实际连接的库，不要凭旧记忆假定实例名与库名；查询报"表不存在"优先怀疑库选错或配置变更。
- 目标库确实不在 MCP 覆盖内时：明确告知用户受限原因，请其调整 mcp.json，或改用代码侧证据（_sql/mysql/system.sql、迁移脚本、git 历史）替代查库。

## 适用/不适用边界
适用：本仓库需校验业务表结构、字段演进、数据分布的场景；以及修改/核对 mcp.json 中 MySQL 实例连接参数的场景。不适用：纯代码/git 分析任务无需动用 MCP；其他项目的 MCP 配置命名与库映射不能照搬本条。
