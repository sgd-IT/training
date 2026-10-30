---
title: "仓库SQL基线脚本不能代表线上表结构须查information_schema"
usage_scenario:
    - "判断某字段是否存在于线上数据库表"
    - "评估文本列长度是否会截断落库内容"
    - "核对数据库迁移脚本是否已执行"
keywords:
    - "SHOW INDEX"
    - "迁移脚本落盘"
    - "information_schema核验"
    - "基线建表脚本陈旧"
    - "增量迁移未回写"
source: "auto"
---

Bug class: 用仓库内的数据库基线脚本推断线上表结构，导致"列不存在"的错误结论

Root cause: 项目 _sql/mysql/system.sql、system_mysql5.7.sql 等基线建表脚本是历史快照，后续通过 V2026xxxx__*.sql 增量迁移新增/改型的列（如 alarm_kind、tier、handle_status，以及 details 由 varchar(255) 变为 text）不会回写基线脚本；同时迁移脚本里写明的动作也可能尚未在线上执行（例：V20261023 声明 DROP COLUMN order_status，但线上 information_schema 查得该列仍存在）。因此"基线脚本+迁移脚本"只能证明代码意图，不能证明线上事实。

Fix pattern: 判断某列是否存在/类型长度是否够用时，以三层交叉验证为准——①ORM 实体（DO）字段与写入点赋值语句；②迁移脚本目录全量扫描确认是否曾加过该列；③线上 information_schema.COLUMNS 按 TABLE_NAME 实际查询列名与 COLUMN_TYPE。只有三者一致才下结论，涉及数据规模（如是否截断、是否有历史脏数据）必须补一条聚合 SELECT 实测。

Reusable lesson: 不要因为在仓库 SQL 文件里 Grep 不到某列就断言"该字段不存在/长度只有 255 会截断"，因为基线脚本陈旧且迁移可能未执行，会把不存在的缺陷报给用户或漏掉真实缺陷；应改查线上 information_schema 与 DO 类。适用于排查"某状态/字段是否落库""某列长度是否会截断""迁移是否已生效"这类结构性问题；不适用于纯代码链路（Controller/Service/前端映射）的判断，那类仍以源码为唯一依据。
