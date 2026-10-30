---
title: "数据库字段 deleteflag 应为 delete_flag"
usage_scenario:
    - "执行SQL查询报Unknown column 'xxx'错误"
    - "Java代码中SQL语句字段名与数据库实际字段不一致"
keywords:
    - "delete_flag"
    - "字段名不匹配"
    - "MySQL列不存在"
---

数据库表中实际字段名为 `delete_flag`，但代码中误写为 `deleteflag`，导致 MySQL 报错 `Unknown column 'deleteflag' in 'field list'`。需统一使用 `delete_flag` 字段名。（来源：CallMcpTool）
