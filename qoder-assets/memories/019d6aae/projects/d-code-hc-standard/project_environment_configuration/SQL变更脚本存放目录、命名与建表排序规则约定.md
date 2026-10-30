---
title: "SQL变更脚本存放目录、命名与建表排序规则约定"
usage_scenario:
    - "新增表或字段时创建变更脚本"
    - "编写MySQL8建表DDL选择排序规则"
    - "排查JOIN报1267排序规则冲突"
keywords:
    - "sql变更脚本目录"
    - "DDL与jar同步上线"
    - "驼峰列名"
    - "utf8mb4_general_ci"
    - "Illegal mix of collations"
    - "CONVERT TO CHARACTER SET"
source: "auto"
---

hc-standard 项目的数据库结构变更脚本统一存放在 ruoyi-admin/src/main/resources/sql/ 目录下，多数采用 add_xxx.sql 风格（例如 add_work_interrupt.sql、add_work_actual_start_time.sql），每个需求对应一个独立脚本文件。功能模块成套交付时也可用「表名.sql + 表名_menu.sql + 表名_data.sql」拆分（如 base_area_risk_unit.sql / base_area_risk_unit_menu.sql / base_area_risk_unit_data.sql），不强制 add_ 前缀。

建表 DDL 必须显式写 COLLATE=utf8mb4_general_ci：只写 DEFAULT CHARSET=utf8mb4 时 MySQL 8 会取该字符集默认的 utf8mb4_0900_ai_ci 而不继承库默认，后续与老表 JOIN 字符串列会报 [HY000][1267] Illegal mix of collations。库里已有 7 张历史表（base_check_content、base_check_record_content、base_device_ctrl、base_duty_schedule、base_file、base_overhaul、base_third_party_unit）就是 0900，属遗留隐患。已建错的表用 ALTER TABLE xxx CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci 修复，不丢数据。

配套事实：base_work_special 等业务表的列名为驼峰风格（workStartTime、actualStartTime、levelShow 等），不是下划线风格；BaseWorkSpecialMapper.xml 使用显式列清单而非 select *，因此新增列必须同时补 resultMap 映射、select 列、insert 列与值、update 片段四处，只加 domain 字段会导致查询取不到值。

由于 mapper 引用了显式新列，DDL 未执行的环境会直接报"列不存在"，脚本必须与 jar 同批上线，不能先发布代码后补 SQL。
