---
title: "wb_contract表缺失contract_no和type字段"
usage_scenario:
    - "MyBatis/MP查询报Unknown column错误且指向特定字段"
    - "Java实体类字段在数据库表中不存在导致SQL语法异常"
    - "排查SQLSyntaxErrorException时需比对表结构与实体类字段"
keywords:
    - "SQLSyntaxErrorException"
    - "contract_no"
    - "wb_contract"
    - "ALTER TABLE"
---

数据库表 `wb_contract` 缺失实体类中引用的字段 `contract_no` 和 `type`，导致 SQL 查询报错 `Unknown column 'contract_no' in 'field list'`。修复命令为：`ALTER TABLE \`wb_contract\` ADD COLUMN \`contract_no\` varchar(100) DEFAULT NULL COMMENT '合同编号' AFTER \`id\`, ADD COLUMN \`type\` char(1) DEFAULT '0' COMMENT '合同类型(0-维保合同 1-巡检合同 2-维修合同 3-其他)' AFTER \`name\`;`（来源：CallMcpTool + Bash）
