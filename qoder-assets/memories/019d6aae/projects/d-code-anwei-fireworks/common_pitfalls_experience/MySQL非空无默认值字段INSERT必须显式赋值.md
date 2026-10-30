---
title: "MySQL非空无默认值字段INSERT必须显式赋值"
usage_scenario: []
keywords:
    - "MySQL"
    - "NOT NULL"
    - "DEFAULT"
    - "UUID"
source: "user"
---

MySQL表中定义为NOT NULL且无DEFAULT值的字段（如alram_info_id），INSERT时必须显式提供值，否则报错[HY000][1364]。常用解决方案是使用REPLACE(UUID(),'-','')生成无短横线UUID字符串。（来源：SQL执行约束）
