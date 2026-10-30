---
title: "push功能模块归属路径"
usage_scenario:
    - "新增push相关功能时确定代码存放位置"
    - "重构或迁移代码时遵循统一模块归属原则"
    - "新成员熟悉项目模块职责划分"
keywords:
    - "push模块"
    - "ruoyi-system"
    - "模块归属"
---

项目采用分层模块化结构，`push` 相关功能（`PushDataController` 和 `PushDataServiceImpl`）应统一归属在 `ruoyi-system` 模块中，具体路径为：
- Controller：`com.ruoyi.base.controller.push`
- Service 实现：`com.ruoyi.base.service.impl`
