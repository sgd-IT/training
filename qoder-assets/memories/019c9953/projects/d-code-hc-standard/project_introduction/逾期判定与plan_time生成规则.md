---
title: "逾期判定与plan_time生成规则"
usage_scenario:
    - "排查逾期记录产生原因时验证判定逻辑"
    - "新增或修改巡检任务配置时预估plan_time生成效果"
    - "设计巡检计划调整方案时评估对逾期数据的影响"
keywords:
    - "逾期判定"
    - "plan_time"
    - "巡检周期"
    - "check_status"
---

项目逾期判定逻辑：当检查记录的`check_status=0`（未检查）且当前时间大于其`plan_time`时，系统自动将`check_status`更新为4（逾期）。`plan_time`不是人工填写，而是由创建者配置的巡检周期参数（`checkCycle`、`checkCycleUnit`、`workStartTime`、`workEndTime`）由定时任务动态计算生成。
