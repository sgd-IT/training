---
title: "应用关闭期Redis异步调用导致Netty executor拒绝任务"
usage_scenario: []
keywords:
    - "Redis"
    - "Netty"
    - "应用关闭"
    - "Lettuce"
---

Spring Boot应用关闭过程中，若异步任务（如CompletableFuture）仍调用RedisTemplate，会因Lettuce底层Netty EventLoopGroup已被关闭而抛出'executor not accepting a task'异常，导致RedisConnectionFailureException。解决方案是在Redis操作外层增加try-catch捕获RedisConnectionFailureException并静默处理。（来源：异常堆栈）
