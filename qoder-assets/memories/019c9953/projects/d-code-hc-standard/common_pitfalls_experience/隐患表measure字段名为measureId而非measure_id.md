---
title: "隐患表measure字段名为measureId而非measure_id"
usage_scenario:
    - "CallMcpTool执行SQL报Unknown column 'measure_id'"
    - "查询隐患数据时因字段名大小写或命名约定错误失败"
keywords:
    - "measureId"
    - "字段名"
    - "CallMcpTool"
    - "SQL错误"
---

隐患主表中 measure 字段名为 'measureId'（驼峰命名），非 'measure_id'。SQL 查询中使用 'measure_id' 会导致 'Unknown column' 错误。正确写法为 'measureId'。（来源：CallMcpTool）
