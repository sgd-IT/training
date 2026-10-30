---
title: "mobileMe 字段语义为 IMEI 码而非手机号"
usage_scenario:
    - "排查 mobileMe 参数验证失败但输入看似符合长度要求"
    - "在数据库或代码中看到 mobileMe 字段时需明确其为 IMEI 而非手机号"
    - "编写 SQL 或后端逻辑时需区分 mobileMe（IMEI）与 phone_number（手机号）"
keywords:
    - "mobileMe"
    - "IMEI"
    - "字段语义"
    - "参数验证"
---

在本项目中，`mobileMe` 字段严格表示设备 IMEI 码（15-17位数字），**不是**手机号（`phone_number` 字段，固定11位）。所有校验逻辑（如 `{mobileMe=mobileMe长度必须为15-17位}`）均基于此语义。若数据库中 `base_risk_task_record.mobile_me` 或 `sys_user.mobile_me` 为空，则 SQL 中 `COALESCE(NULLIF(r.mobile_me, ''), (select u.mobile_me from sys_user u where u.nick_name = r.create_by limit 1))` 返回 NULL，导致推送至园区平台时校验失败。（来源：SQL 查询逻辑与数据库字段实际值分析）
