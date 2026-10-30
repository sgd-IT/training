---
title: "MyBatis-Plus实体字段映射导致Unknown column错误的根因与修复"
usage_scenario: []
keywords:
    - "MyBatis-Plus"
    - "Unknown column"
    - "SQLSyntaxErrorException"
    - "字段映射"
    - "数据库同步"
    - "TableField"
source: "auto"
---

MyBatis-Plus默认将实体类中所有未标注@TableField(exist = false)的字段自动按驼峰转下划线规则映射为数据库列名；若数据库表实际缺少该列（如新增字段pullType → pull_type、capsizeNum → capsize_num、mobile等），执行SQL时将抛出'Unknown column' SQLSyntaxErrorException错误。

根本原因：实体类与数据库表结构长期不一致，新增字段后未同步ALTER TABLE ADD COLUMN补全缺失列。

解决方案：
1. 数据库执行ALTER TABLE ADD COLUMN补全缺失列（推荐，保障数据一致性）；
2. 实体类字段上加@TableField(exist = false)禁用映射（仅适用于非持久化字段，如mobile）。

关键教训：实体类新增字段后，必须同步检查并维护数据库表结构。
