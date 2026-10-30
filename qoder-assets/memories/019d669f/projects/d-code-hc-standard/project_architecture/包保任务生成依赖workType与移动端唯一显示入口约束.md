---
title: "包保任务生成依赖workType与移动端唯一显示入口约束"
usage_scenario:
    - "改造包保任务生成或识别逻辑时"
    - "判定某任务或记录是否为包保任务"
    - "评估移动端包保功能可达性与入口改动"
keywords:
    - "addRiskTaskRecord"
    - "workType负责人任务"
    - "is_defend包保判定"
    - "风险排查唯一入口"
    - "getType分tab可见性"
    - "包保记录量级核对"
source: "auto"
---

## Notes（追加）
- 当天提前生成排查记录的正规入口是 PC「系统监控→定时任务」（sys_menu menu_id=110，component=monitor/job/index）对 job 104（invoke_target='TaskList.addRiskTaskRecord'）点行末"更多→执行一次"（前端 handleRun → runJob(jobId,jobGroup) → PUT /monitor/job/run），按钮受 monitor:job:changeStatus 权限控制；不改 cron、不影响后续排班，生成的记录其 task_next_time 自动推到下一周期故不会重复。
- 但该动作的作用范围是全库扫描 base_risk_task 中 deleted=0 AND status=0 AND task_next_time<=NOW() 的全部配置，不针对某人某单元：实测本轮除液氯 28 条外还连带锅炉房 risk_unit_id=d6a63671… 的 6 条日常任务一并补出。故交付此方案前必须先按 risk_unit_id GROUP BY 统计到期配置并向用户预告连带范围；健康度核查用 SELECT status,create_time FROM sys_job_log WHERE invoke_target='TaskList.addRiskTaskRecord' ORDER BY create_time DESC（status=0 为成功）。
