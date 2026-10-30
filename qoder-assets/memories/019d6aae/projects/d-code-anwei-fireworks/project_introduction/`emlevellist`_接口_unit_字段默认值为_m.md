---
title: "`/em/level/list` 接口 unit 字段默认值为 m"
usage_scenario:
    - "对接口返回数据格式进行一致性审查时"
    - "修复或新增类似默认值逻辑时参考统一实现方式"
    - "新成员理解 Level 实体字段默认行为"
keywords:
    - "unit默认值"
    - "Level实体"
    - "getUnit重写"
    - "/em/level/list"
---

项目 `/em/level/list` 接口返回的 `unit` 字段在数据库值为 null 或空字符串时，默认显示为 `"m"`，该逻辑通过在 `Level` 实体类中重写 `getUnit()` 方法实现，不修改 Controller 或 Service 层。
