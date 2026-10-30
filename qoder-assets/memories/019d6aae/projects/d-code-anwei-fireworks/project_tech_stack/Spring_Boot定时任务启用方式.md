---
title: "Spring Boot定时任务启用方式"
usage_scenario:
    - "新增或迁移定时任务时选择调度框架"
    - "排查定时任务不触发问题时确认是否启用调度注解"
    - "代码审查中检查定时任务配置是否符合项目规范"
keywords:
    - "Spring Boot"
    - "@EnableScheduling"
    - "@Scheduled"
    - "fixedDelay"
---

项目基于Spring Boot框架，通过`@EnableScheduling`启用定时任务功能，Job类使用`@Scheduled(fixedDelay = 2000)`实现每2秒执行一次的轮询逻辑。
