---
title: "API访问日志写入链路溯源与清理任务实际未生效的核实"
usage_scenario:
    - "排查 infra_api_access_log 数据增长或字段来源"
    - "判断 JobHandler 定时任务是否真的在运行"
    - "评估访问日志保留期对统计口径的影响"
keywords:
    - "infra_api_access_log"
    - "ApiAccessLogFilter"
    - "AccessLogCleanJob"
    - "Quartz RAMJobStore"
    - "infra_job 状态暂停"
    - "Scheduled 注解对照"
source: "auto"
---

（原正文保持历史快照不变）

## Notes
- 【2026-10-24 更正】正文第 7 步"全库 grep 确认项目 SQL 种子数据并无该 INSERT"不准确。本轮 Grep 复核 `_sql/mysql/system.sql`（约 L1117）实际存在该种子行：`INSERT INTO infra_job VALUES (25, '访问日志清理 Job', 2, 'accessLogCleanJob', '', '0 0 0 * * ?', ...)`，且 postgresql/enjoy-iot.sql、system_mysql5.7.sql 同样含该 INSERT。最新结论应为：清理任务未生效的根因不是"库里缺行"，而是**初始化脚本种子里 status 天生就是 2（JobStatusEnum.STOP 暂停）**，需运维手动开启；其余结论（infra_job_log 0 条、表堆积 107 万条、RAMJobStore 重启需 POST /infra/job/sync）均继续成立。
- 【2026-10-24 补充】infra_api_access_log 表上唯一索引为 `idx_create_time(create_time)`（见 system.sql 建表段），而分页接口过滤的是 begin_time、排序用的是 id DESC，三者全部错位；后续做查询优化时以此为准。
