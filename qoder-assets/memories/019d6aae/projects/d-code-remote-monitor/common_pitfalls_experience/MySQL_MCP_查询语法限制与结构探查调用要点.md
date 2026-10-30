---
title: "MySQL MCP 查询语法限制与结构探查调用要点"
usage_scenario:
    - "通过 MCP 查询表结构或按列名过滤元数据"
    - "MCP 报 Parsing failed 或 Table doesn't exist 错误"
    - "跨环境比对同一张表数据差异"
keywords:
    - "MySQL MCP"
    - "information_schema"
    - "SHOW语法限制"
    - "COUNT先行"
    - "跨库比对"
source: "auto"
---

Bug class: MySQL MCP（工具 mysql_query，只接收单个 sql 字符串）执行元数据/列查询时语句解析或列名报错。

Root cause: 该 MCP 前置了一个受限 SQL 解析器，只接受常规语句形态。两类写法必然失败：① `SHOW COLUMNS FROM t WHERE Field IN (...)` —— 报 "Parsing failed: Expected [A-Za-z0-9_$...] but \" \" found"，SHOW 家族不支持 WHERE 过滤；② 凭记忆猜列名写 SELECT（如以为告警记录表有 source 列）—— 报 "Unknown column ... in 'field list'"。两者都不是数据库本身的问题，而是工具层拦截，重试同样语句无意义。

Fix pattern: 需要按列名筛选元数据时改用 `information_schema.columns` 条件查询（按 TABLE_SCHEMA=DATABASE() 与 TABLE_NAME 过滤、ORDINAL_POSITION 排序），可一次拿到列名、列类型、可空性、默认值与 EXTRA；或直接 `SHOW COLUMNS FROM t` 取全量后自行判断；写业务 SELECT 前先跑一次全量 SHOW COLUMNS / 读 DO 类源码确认列名，再落查询。对可能行数较多的表先执行聚合计数（COUNT）确定规模，再决定是否拉全量明细，避免单次返回超长内容被截断；跨库比对时把两侧同构的 SELECT 放在同一轮并行发出，减少往返。

Reusable lesson: Don't assume a MySQL syntax that works in a CLI client will pass the MCP's SQL parser, because non-standard extensions (SHOW ... WHERE) are rejected at parse time and repeated retries only waste rounds; instead express metadata filtering via information_schema and verify exact column names before writing SELECT. Applies to all read/verify queries through this project's MySQL MCP instances; does not apply when executing DDL/SQL scripts through the documented local mysql CLI fallback path.
