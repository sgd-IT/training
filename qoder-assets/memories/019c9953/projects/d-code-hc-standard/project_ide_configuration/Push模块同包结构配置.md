---
title: "Push模块同包结构配置"
usage_scenario:
    - "迁移或重构push相关类时确定包路径"
    - "新成员理解push模块代码组织方式"
    - "避免跨包依赖导致的编译错误"
keywords:
    - "push包"
    - "同包调用"
    - "com.ruoyi.base.controller.push"
---

PushDataController和PushDataServiceImpl两个类需放在同一个Java包com.ruoyi.base.controller.push下，实现同包调用无需import
