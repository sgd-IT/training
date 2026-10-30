---
title: "音柱device_code逗号分隔多值解析规范"
usage_scenario: []
keywords:
    - "device_code"
    - "逗号分隔"
    - "split"
    - "定向播报"
source: "user"
---

音柱定向播报需支持数据库中device_code字段逗号分隔多值（如"9C0CA428,B270E069,9E9AB31B"）的解析，必须使用flatMap + split(",") + trim方式拆分为独立设备编码列表，再进行定向播报。
