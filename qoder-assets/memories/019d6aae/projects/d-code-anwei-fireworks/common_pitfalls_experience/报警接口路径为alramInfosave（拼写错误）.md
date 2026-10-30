---
title: "报警接口路径为/alramInfo/save（拼写错误）"
usage_scenario: []
keywords:
    - "alramInfo"
    - "404"
    - "接口路径"
    - "read_file"
source: "user"
---

该项目中报警保存接口的实际路径为 `/alramInfo/save`（`alram` 拼写错误，非标准 `alarm`），调用时必须使用该路径，否则返回404。（来源：read_file读取TokenController.java源码确认）
