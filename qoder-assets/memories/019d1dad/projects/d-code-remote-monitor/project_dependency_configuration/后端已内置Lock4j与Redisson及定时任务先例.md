---
title: "后端已内置Lock4j与Redisson及定时任务先例"
usage_scenario:
    - "设计跨实例互斥或防并发击穿方案时"
    - "新增周期定时任务选择实现方式时"
    - "评估是否需要引入新的分布式依赖时"
keywords:
    - "lock4j"
    - "redisson"
    - "Scheduled定时任务"
    - "spring-boot-starter-protection"
    - "分布式锁"
source: "auto"
---

后端已具备分布式能力基础设施，无需新增依赖：
- dependencies/pom.xml 声明 lock4j.version=2.2.7、redisson.version=3.41.0；framework/spring-boot-starter-protection 引入 lock4j（Redisson 版），可直接用 @Lock4j 注解做跨进程互斥，application-prod.yaml 已有 lock4j 配置项（acquire-timeout: 3000、expire: 30000）。
- framework/spring-boot-starter-redis 引入 redisson，业务代码可直接注入 RedissonClient（框架 mq 的 RedisPendingMessageResendJob 已有 getLock 用法）。
- @Scheduled 定时任务已是项目既有模式，firewatch 域存在 FirewatchScheduledTask，另有 DeviceStateCheckTask 等先例。
因此若需跨实例互斥或"多副本下只跑一份"的周期任务，优先复用 @Lock4j + @Scheduled，而非手写 Redis setIfAbsent 锁。
