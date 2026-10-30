---
title: "通道号存储基准为1-based"
usage_scenario: []
keywords:
    - "channel_no"
    - "1-based"
    - "通道号"
    - "数据库约定"
---

数据库表 `bz_camera.channel_no` 字段存储的是1-based通道号（即通道1对应值'1'），该约定适用于所有依赖该字段的业务逻辑，包括HLS录像回放URL拼接和实时拉流通道匹配。
