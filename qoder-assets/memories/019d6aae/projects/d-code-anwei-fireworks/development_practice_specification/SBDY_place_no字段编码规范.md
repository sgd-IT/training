---
title: "SBDY place_no字段编码规范"
usage_scenario: []
keywords:
    - "SBDY"
    - "place_no"
    - "15位编码"
    - "省平台校验"
---

SBDY（设备信息）表中place_no字段必须严格使用15位仓库编码（如520526920002101），禁止使用18位库房编码（如520526920002101001）。该字段不接受回退逻辑或条件优先策略，任何18位编码输入均会导致SBDY→ZBDY→ZBSS→ZBBJ级联校验失败（共14条错误）。
