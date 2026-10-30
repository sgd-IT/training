---
title: "wb_contract表结构补充字段"
usage_scenario:
    - "部署新环境时执行数据库初始化脚本"
    - "排查Unknown column异常时确认表结构完整性"
    - "维护建表脚本与实际数据库结构一致性"
keywords:
    - "wb_contract"
    - "contract_no"
    - "type"
    - "数据库表结构"
    - "ALTER TABLE"
---

数据库表`wb_contract`需包含`contract_no`和`type`字段:
- `contract_no`: varchar(100), DEFAULT NULL, COMMENT '合同编号', 插入位置在`id`之后
- `type`: char(1), DEFAULT '0', COMMENT '合同类型(0-维保合同 1-巡检合同 2-维修合同 3-其他)', 插入位置在`name`之后
