---
title: "本机执行 SQL 一律走 MCP，禁用写文件加命令行兜底"
usage_scenario:
    - "向数据库插入或修改含中文数据"
    - "MCP 语句被语法过滤拦截时找替代写法"
    - "经用户批准需用命令行只读兜底查询时"
keywords:
    - "MCP mysql_query 执行 SQL"
    - "information_schema COLUMNS 先查列结构"
    - "环境变量传密码"
    - "递归 CTE 版本兼容"
    - "SHOW TABLES 语法过滤"
source: "auto"
---

## Usage Scenario
Description: 在本机（Windows + Qoder）向 anwei-office 等 MySQL 库执行 SQL 时选择执行手段。
Examples: 插入或修改含中文的数据；盘点表名列结构；MCP 语句被语法过滤拦截时找替代写法。

## Usage Method
Bash（调用数据库 CLI）：一律优先走 MCP mysql_query（参数为 JSON、UTF-8 无损），禁止用「写 .sql 文件 + mysql.exe source」的兜底方式——本机把 .sql 文件落盘后内容会变形（曾被写为乱码/密文，重写为 UTF-8 无 BOM 仍无效），CLI 读取后执行失败。仅当 MCP 确实覆盖不到且用户明确批准兜底时，才用命令行做只读查询：先用环境变量方式传入密码再执行，避免凭据出现在命令文本里；对服务端版本没把握时不要直接使用 WITH RECURSIVE 等较新语法，改写成多条互相独立的普通查询用分号在同一次调用里连续执行；追溯父子链优先用「按已知 id 集合批量取父级」的两段式查询代替递归查询。
mysql_query 对列名零容错，必须先 SELECT COLUMN_NAME, DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME='表名' 拿到真实列结构再写过滤条件；同理盘点表名用 information_schema.TABLES 的 LIKE 条件替代被拦截的 SHOW TABLES。这条"先摸结构再查询"的前置顺序在跨两套同名业务表（如 crm_product 与 erp_product）做归属判定时尤其关键，因为两表列名往往不一致。

## Notes
- 把密码写在命令行参数里会让凭据留在命令历史与输出中，必须通过环境变量传给客户端
- 对不支持递归公共表表达式的版本直接写 WITH RECURSIVE 会报语法错误并浪费一轮调试，必须先确认服务端大版本或改用普通多语句逐层取父级
- 全局搜索某个仅在数据库配置里被引用的页面文件名会得到零命中并误判为文件未被使用，应先确认该引用是否存放在数据库表中
- 凭直觉猜列名（如在 crm_product 用 code 过滤、在 system_users 查 name）必然报 Unknown column，先查 COLUMNS 是必要前置步骤
