---
title: "MyBatis-Plus 3.2.0 默认更新策略"
usage_scenario:
    - "分析接口无法清空数据库字段的问题时"
    - "排查MyBatis-Plus动态SQL生成逻辑"
    - "审查实体字段更新行为是否符合业务预期"
keywords:
    - "MyBatis-Plus"
    - "FieldStrategy.NOT_NULL"
    - "updateById"
    - "全局默认策略"
---

项目使用 MyBatis-Plus 3.2.0，其全局字段更新策略默认为 FieldStrategy.NOT_NULL，即实体字段为 null 时不参与 UPDATE SQL 的 SET 子句生成。
