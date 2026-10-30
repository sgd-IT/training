---
title: "MySQL MCP 连接写权限差异与元数据查询语法限制"
usage_scenario:
    - "在MCP连接上执行删改前确认写权限"
    - "MCP中探查表清单或列名报解析错误"
    - "多环境同名库写入前确认连接落点"
keywords:
    - "mysql_query"
    - "MCP写权限差异"
    - "information_schema"
    - "READ-ONLY连接"
    - "连接落点探测"
source: "auto"
---

## Usage Scenario
Description: 用 MySQL 类 MCP 服务做数据核查、元数据探查与删改时，需要先判定目标 serverName 是否具备写权限、确认连接落点环境、以及元数据查询该用什么语法
Examples: 定位某账号的业务数据后执行删除；在多环境库之间确认哪个连接可写；拿一张业务表的全部列名/类型/主键/注释

## Usage Method
mysql_query（MySQL MCP）：
- 写权限判定：同一 MCP 包下的不同 serverName 权限可以完全不同——一个连接串可能开放 INSERT/UPDATE/DELETE，另一个是 READ-ONLY。动手写操作前必须先读该 serverName 的 tools/mysql_query.json 描述确认允许的操作类型，不能凭"能连上同一个库"推断可写；只读连接上只能跑 SELECT。
- 落点验证：写入前先跑一条只读探测（当前连接的主机名与库名变量）确认落在哪个环境，同名库常在测试机与正式机并存。
- 元数据探查优先走 information_schema（tables / columns 按 table_name IN (...) 一次取多表列名，取 COLUMN_NAME/COLUMN_TYPE/IS_NULLABLE/COLUMN_KEY/EXTRA/COLUMN_COMMENT），因为该 MCP 自带 SQL 解析器不接受 `SHOW TABLES LIKE ...`、`SHOW COLUMNS` 这类语句（直接报 Parsing failed）。列名不要凭常见命名习惯猜，先查列清单再写条件。
- 跨多表计数用一条 UNION ALL 汇总，比逐表多次调用更省轮次。INSERT 支持标准单行 VALUES 形式，批量造数拆成多条单行插入而非多值 VALUES，避免解析器不兼容。

## Notes
- 凭 serverName 名字或"能查成功"假定其可写导致 DELETE 被拒或误写到只读实例，必须先读工具 schema 里的权限说明再决定在哪个连接执行写操作
- 未先用只读探测确认主机名与库名就对同名库所在实例写入，存在误写正式环境的风险
- 用 SHOW TABLES / SHOW COLUMNS 探查表清单导致解析报错白耗一轮，必须改用 information_schema.tables 与 information_schema.columns
- 一次 SELECT 多个不相干表的列会因个别列不存在整条语句失败，必须按表拆成独立查询以便单独修正列名
