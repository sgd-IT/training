---
title: "SyncShuangKongTask定时任务统一双cron调度"
usage_scenario:
    - "修改定时任务调度策略时统一cron表达式"
    - "新成员理解各同步任务执行时间点"
    - "运维排查任务未触发原因时核对调度配置"
keywords:
    - "SyncShuangKongTask"
    - "cron表达式"
    - "23:30"
    - "06:00"
    - "定时任务"
---

SyncShuangKongTask中以下4个定时任务统一采用双cron调度：
- syncRiskObj()：每天23:30和06:00执行
- syncRiskUnit()：每天23:30和06:00执行
- syncRiskEvent()：每天23:30和06:00执行
- syncRiskTask()：每天23:30和06:00执行
（syncRiskMeasure和syncRiskTaskRecord已按此配置，无需改动）
