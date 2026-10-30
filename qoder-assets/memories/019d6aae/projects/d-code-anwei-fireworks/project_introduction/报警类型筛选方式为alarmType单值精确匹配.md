---
title: "报警类型筛选方式为alarmType单值精确匹配"
usage_scenario:
    - "对接前端报警筛选需求时确认参数传递方式"
    - "排查报警筛选结果异常时验证后端查询逻辑"
    - "编写接口文档时明确筛选参数语义"
keywords:
    - "alarmType"
    - "精确筛选"
    - "单值匹配"
    - "报警接口"
---

报警列表接口 `/alarm/center/event/list` 的前端筛选方式为直接传入 `alarmType` 单值参数（1~8），后端通过 `eq` 精确匹配 `ai.alarm_type` 字段进行筛选，不启用大类分组逻辑。
