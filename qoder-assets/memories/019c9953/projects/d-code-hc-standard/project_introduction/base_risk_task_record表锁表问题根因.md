---
title: "base_risk_task_record表锁表问题根因"
usage_scenario:
    - "排查巡检状态更新失败等数据库写入异常时定位锁表原因"
    - "优化定时同步任务时确定需改造的事务边界和提交粒度"
    - "进行数据库稳定性治理时识别高风险长事务场景"
keywords:
    - "base_risk_task_record"
    - "锁表"
    - "长事务"
    - "定时同步任务"
---

项目存在`base_risk_task_record`表锁表问题：
- 根本原因是定时同步任务（如双控同步、排查记录同步）对本表执行长事务批量操作，未分批提交，导致行锁长时间持有
- 凌晨`sys_job_log`写入慢（1~39秒）加剧I/O压力，进一步延长事务持锁时间
- 表现为`MySQLTransactionRollbackException: Lock wait timeout exceeded`，影响巡检状态等UPDATE操作
