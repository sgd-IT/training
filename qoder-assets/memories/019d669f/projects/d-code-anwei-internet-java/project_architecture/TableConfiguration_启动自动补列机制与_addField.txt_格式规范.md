---
title: "TableConfiguration 启动自动补列机制与 addField.txt 格式规范"
usage_scenario:
    - "排查 MyBatis-Plus Unknown column 报错"
    - "为实体新增数据库字段并确保存量库兼容"
    - "编写和维护项目自动迁移脚本"
keywords:
    - "TableConfiguration"
    - "addField.txt"
    - "自动补列"
    - "schema migration"
    - "启动初始化"
source: "auto"
---

项目内置的 TableConfiguration 启动时自动执行 schema 迁移：按行读取 template/master/addField.txt（或 template/iot/addField.txt），每行格式为 "表名|字段名|ALTER语句"；若目标表中该字段不存在，则执行 ALTER 语句。该机制是解决实体字段新增后存量库缺列问题的核心自动化方案，适用于 master 和 iot 两套数据源模板。
