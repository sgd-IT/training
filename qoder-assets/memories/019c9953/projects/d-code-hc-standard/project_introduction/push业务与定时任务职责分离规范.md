---
title: "push业务与定时任务职责分离规范"
usage_scenario:
    - "新增push功能时选择正确模块和包路径"
    - "设计新定时任务时避免与push包耦合"
    - "代码审查时检查模块边界是否被破坏"
keywords:
    - "职责分离"
    - "push模块"
    - "定时任务模块"
    - "ruoyi-system"
    - "ruoyi-quartz"
---

push业务逻辑（PushDataController、PushDataServiceImpl）与定时任务（SyncShuangKongTask）必须分离在不同模块和包中：push类归属ruoyi-system模块的controller/push包，定时任务归属ruoyi-quartz模块的quartz.task包，禁止合并为同一包或模块
