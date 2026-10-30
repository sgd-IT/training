---
title: "温湿度推送功能缺陷：device_code字段误存device_type"
usage_scenario: []
keywords:
    - "温湿度推送"
    - "device_code"
    - "device_type"
    - "字段误用"
    - "ZBSS"
source: "user"
---

温湿度实时数据推送功能存在关键缺陷：`em_humidity`/`em_temperature` 表中 `device_code` 字段错误地存储了 `device_type` 的值（如3、4），而非正确的18位设备编码（如520281920001101），导致 `PushingGZDataService` 推送时按完整设备编码查询永远为空，温湿度实时数据（ZBSS）无法正常推送。
