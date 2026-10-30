---
title: "告警双读 SQL 的 collation 坑：跨文本列统一比较必须走数字域"
usage_scenario:
    - "告警字段双读 SQL 改造"
    - "MySQL 跨类型列统一表达式比较"
    - "SQL 变更落代码前的形态验证"
keywords:
    - "collation 冲突"
    - "COALESCE 双读"
    - "CAST AS UNSIGNED"
    - "MySQL 隐式转换"
    - "SQL 形态预验证"
source: "auto"
---

Bug 类型：MySQL 报 `Illegal mix of collations (utf8mb4_bin / utf8mb4_general_ci vs utf8mb4_unicode_ci)`——在本项目 eiot_alert_record 双读改造中，对 `COALESCE(CAST(alarm_kind AS CHAR), level)` 再做 `= 'x'` 或与 level 列比较时必然报错。

根因：旧列 level 为 varchar 且 collation=utf8mb4_unicode_ci；CAST(数字 AS CHAR) 产生的临时字符串 collation 固定为 bin（部分场景 general_ci），两个 IMPLICIT 级 collation 不可强转，任何"字符串域统一表达式"参与比较都会崩。

修复模式：跨新旧列的双读统一用**数字域**——`COALESCE(alarm_kind, CAST(level AS UNSIGNED))`。GROUP BY 直接用它（已验证）；筛选比较也用它（`= {0}`，字符串参数走 MySQL 隐式数值转换，全程无 collation 参与）。前提：level 值域须实测为纯数字（本项目实测仅 1/2/3/4，无脏值）。

可复用教训：跨类型兼容的"统一键"SQL 表达式优先选数值域而非字符串域；SQL 双读/迁移改造落代码前，先在真实库（MCP）跑形态与等价性预验证——本次预验证阶段即抓到该坑并逐格证明了新旧口径一致，避免带病上线。边界：若 level 出现非数字脏值，CAST AS UNSIGNED 会将其变为 0，需重新评估。
