---
title: "ORM SQL字段必须与数据库表结构严格一致"
usage_scenario: []
keywords:
    - "MyBatis"
    - "SQL语法错误"
    - "字段缺失"
    - "数据库表结构"
---

MyBatis/MyBatis-Plus等ORM框架中，SQL语句中引用的字段必须在对应数据库表中真实存在，否则运行时抛出BadSqlGrammarException（如'Unknown column xxx in field list'）。修复路径应为：先查表结构确认缺失字段，再ALTER TABLE添加，而非修改SQL或实体类。（来源：SQL执行错误日志）
