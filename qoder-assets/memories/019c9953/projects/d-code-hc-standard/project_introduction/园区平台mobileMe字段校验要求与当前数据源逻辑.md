---
title: "园区平台mobileMe字段校验要求与当前数据源逻辑"
usage_scenario:
    - "对接园区平台接口时确认mobileMe字段语义与格式要求"
    - "排查mobileMe参数验证失败问题时复现数据链路"
    - "改造数据查询SQL以满足IMEI码填充需求"
keywords:
    - "mobileMe"
    - "IMEI码"
    - "参数验证失败"
    - "COALESCE数据源"
---

园区平台`/openapi/check_unit_data/troubleshooting`接口要求`mobileMe`字段为IMEI码，长度必须为15-17位；当前数据来源SQL为`COALESCE(NULLIF(r.mobile_me, ''), (select u.mobile_me from sys_user u where u.nick_name = r.create_by limit 1))`，但`base_risk_task_record.mobile_me`和`sys_user.mobile_me`均为空，导致推送`mobileMe=null`而校验失败。
