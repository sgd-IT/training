---
title: "贵州推送access_status更新规则"
usage_scenario:
    - "实现推送成功后access_status字段更新逻辑时"
    - "验证推送状态标记是否符合业务要求"
    - "对接新省份或调整推送状态码时参考现有规则"
keywords:
    - "贵州推送"
    - "access_status"
    - "状态更新"
---

贵州推送逻辑中，当前代码将推送成功后的access_status设为2（增量类型）或不更新（全量/基础类型），而用户期望统一设为1。
