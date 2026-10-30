---
title: "SBDY place_no编码长度规范"
usage_scenario: []
keywords:
    - "SBDY"
    - "place_no"
    - "仓库编码"
    - "15位"
    - "省平台校验"
source: "user"
---

省平台校验要求：SBDY（设备信息）表中的place_no字段必须为15位仓库编码（如520526920002101），禁止使用18位库房编码（如520526920002101001），否则触发级联报错（SBDY→ZBDY→ZBSS→ZBBJ共14条错误）。
