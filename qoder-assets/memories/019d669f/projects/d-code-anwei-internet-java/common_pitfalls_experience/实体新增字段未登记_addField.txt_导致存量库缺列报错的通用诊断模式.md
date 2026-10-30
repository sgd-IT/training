---
title: "实体新增字段未登记 addField.txt 导致存量库缺列报错的通用诊断模式"
usage_scenario:
    - "排查 MyBatis-Plus Unknown column 报错"
    - "诊断实体字段新增后存量库缺列问题"
keywords:
    - "Unknown column"
    - "addField.txt"
    - "TableConfiguration"
    - "schema migration"
source: "auto"
---

Bug class: MyBatis-Plus 实体新增字段后，存量库缺列导致整条业务链路 SQL 报错（BadSqlGrammarException: Unknown column）
Root cause: AlarmInformation 和 CastingWellInfo 实体分别在提交 d146568 和 a7a3326 中新增了字段，但均未登记到启动自动补列文件 addField.txt，也未同步建表模板，导致旧部署库缺失字段
Fix pattern: 在 addField.txt 中为缺失字段添加 "表名|字段名|ALTER语句" 行，并在对应建表模板中补齐字段定义；重启应用后 TableConfiguration 自动执行缺失列的 ALTER
Reusable lesson: Don't assume a single-entity or single-table issue when encountering an Unknown column error; instead, verify whether the same root cause (missing addField.txt registration) applies across multiple entities and tables. Applies when debugging MyBatis-Plus Unknown column errors in this project; does not apply to generic database schema drift outside the TableConfiguration mechanism.
