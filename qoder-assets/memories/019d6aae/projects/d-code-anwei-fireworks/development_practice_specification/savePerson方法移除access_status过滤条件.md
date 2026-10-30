---
title: "savePerson方法移除access_status过滤条件"
usage_scenario:
    - "调整人员数据推送逻辑时参考已验证的过滤条件简化方案"
    - "排查人员数据未被推送问题时确认是否需保留access_status校验"
    - "重构其他类似推送方法（如saveStore、saveDevice）时借鉴相同实践"
keywords:
    - "savePerson"
    - "access_status"
    - "查询条件"
source: "auto"
---

在人员推送方法savePerson()中，移除对access_status字段的等值查询条件.eq(Personnel::getAccessStatus, 1)，仅保留company_code和delete_flag两个过滤条件。
