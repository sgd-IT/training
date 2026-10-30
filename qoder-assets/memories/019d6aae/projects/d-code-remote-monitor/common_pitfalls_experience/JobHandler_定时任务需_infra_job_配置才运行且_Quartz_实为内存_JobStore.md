---
title: "JobHandler 定时任务需 infra_job 配置才运行且 Quartz 实为内存 JobStore"
usage_scenario:
    - "判断某个清理或维护定时任务是否真的在运行"
    - "依据日志表保留期做统计口径或容量规划前"
    - "排查应用重启后定时任务不再触发"
keywords:
    - "JobHandler 不会自触发"
    - "infra_job status 暂停"
    - "RAMJobStore 重启丢失"
    - "job sync 手动同步"
    - "定时任务生效核验"
source: "auto"
---

Bug class: 框架自带的定时清理/维护任务被误认为"自动生效"，实际长期未运行导致数据无限堆积

Root cause: 在该 Quartz+JobHandler 架构下，一个 implements JobHandler 的 @Component 类只是"可被反射调用的执行体"，本身没有自触发能力（无 @Scheduled）。真正触发依赖三件事同时成立：① infra_job 表存在 handler_name 对应的行且 status 为正常（STOP=2 即永久不跑）；② 该 job 已注册进 Scheduler；③ 调度器使用持久化 JobStore。项目里 dev/prod yaml 均 exclude 了 QuartzAutoConfiguration，使 spring.quartz.job-store-type: jdbc 整段配置失效，Scheduler 改由手工 new SchedulerFactoryBean() 提供且未设 DataSource → 退化为 RAMJobStore，应用每次重启调度清零，必须手动调用 POST /infra/job/sync 才会重新装载（syncJob 全库仅此一个调用入口，无启动期自动同步）。种子 SQL 也不含这些清理 job 的 INSERT，全靠运行库手工添加。

Fix pattern: 判断任何 XxxJob 是否真的在跑，必须双证据核验 —— 查 infra_job.status 是否为正常态 + 查 infra_job_log 是否有执行记录（全表 0 条即从未跑过）；再用业务表本身的体量/时间跨度做第三方印证（如日志表跨 5 个月百万条 vs 名义保留 14 天）。要真正启用时，优先把任务改写为 Spring @Scheduled(cron=...)，与项目内其他实际生效的任务（FirewatchScheduledTask、DeviceStateCheckTask 等，均由 @EnableScheduling 驱动、完全不依赖 infra_job/Quartz）保持一致，避免依赖需重启后手动同步的内存 JobStore。

Reusable lesson: Don't assume "框架里有这个 JobHandler 所以策略自动生效" because the job may be paused in DB and the scheduler is in-memory so it never fires, causing silent data accumulation; instead verify infra_job.status + infra_job_log before relying on any retention/cleanup behavior in stats or capacity planning. Applies when reasoning about any implements JobHandler task in this project's infra_job/Quartz mechanism; does not apply to tasks annotated with Spring @Scheduled, which do run automatically on restart.
