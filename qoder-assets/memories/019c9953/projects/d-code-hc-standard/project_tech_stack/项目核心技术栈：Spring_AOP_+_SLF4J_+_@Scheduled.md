---
title: "项目核心技术栈：Spring AOP + SLF4J + @Scheduled"
usage_scenario:
    - "编写新切面时确认技术选型和API用法"
    - "配置日志输出时参考已有Logger命名规范"
    - "实现定时逻辑时选择正确的调度注解"
keywords:
    - "Spring AOP"
    - "SLF4J"
    - "@Scheduled"
    - "切面编程"
---

项目使用Spring AOP实现切面编程，依赖`@Aspect`、`@Around`、`@Pointcut`注解；日志统一使用SLF4J（`LoggerFactory.getLogger("sys-platment")`）；定时任务基于Spring `@Scheduled`注解驱动。
