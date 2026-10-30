---
title: "温湿度报警（ZBBJ）数据源表及完整筛选条件"
usage_scenario:
    - "修正ZBBJ推送逻辑时确定正确数据源"
    - "排查ZBBJ数据缺失或错误时验证表和条件"
    - "编写或审查数据接入文档时确认权威数据源"
keywords:
    - "ZBBJ"
    - "em_early_warning_info_push_record"
    - "warning_type"
    - "pushType"
    - "数据源表"
    - "early_warning_info_id"
---

温湿度报警（ZBBJ）数据的实际来源表是 anwei_mon_fireworks.em_early_warning_info_push_record，核心筛选条件为：warning_type = 0（湿度）或 1（温度），pushType = 0（未下发），且 early_warning_info_id 非空。该表替代了原代码中使用的`em_alram_info`表。
