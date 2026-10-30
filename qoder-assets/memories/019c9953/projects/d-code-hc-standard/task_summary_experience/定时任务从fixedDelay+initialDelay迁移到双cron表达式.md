---
title: "定时任务从fixedDelay+initialDelay迁移到双cron表达式"
usage_scenario:
    - "将固定间隔调度改为多个固定时间点执行"
    - "Spring定时任务需支持每日不同时段触发的改造"
    - "排查因initialDelay导致首次执行延迟的业务场景"
keywords:
    - "@Scheduled"
    - "cron表达式"
    - "定时任务迁移"
---

## 任务描述
将SyncShuangKongTask中的syncRiskTaskRecord方法从fixedDelay+initialDelay调度方式，改为每天23:30和06:00两个固定时间点执行。

## 执行过程
```mermaid
graph TD
    A[需求:取消启动延迟并改为每日固定时间] --> B[读取SyncShuangKongTask.java确认原@Scheduled配置]
    B --> C[识别initialDelay=180000+fixedDelay=21600000组合]
    C --> D[决策改用双@Scheduled+cron表达式]
    D --> E[构造cron="0 30 23 * * ?"和"0 0 6 * * ?"]
    E --> F[执行SearchReplace替换原注解]
```

## 任务总结
成功迁移定时策略：移除initialDelay和fixedDelay，通过两个独立@Scheduled注解实现每日23:30与06:00精准触发，避免了单cron无法表达非连续时段的限制，且确保应用启动后无需等待即可按计划执行。
