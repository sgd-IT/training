---
title: "@PostConstruct中调用SpringApplication.exit()导致伪异常"
usage_scenario:
    - "Spring Boot启动报'ApplicationContext has been closed already'但无明确根因"
    - "@PostConstruct方法中主动退出应用后出现大量BeanCreationException嵌套"
    - "配置校验失败但错误日志被容器关闭异常淹没"
keywords:
    - "@PostConstruct"
    - "SpringApplication.exit"
    - "ApplicationContext closed"
    - "启动伪异常"
---

Spring Boot 启动时若在 `@PostConstruct` 方法中调用 `SpringApplication.exit()`，会导致 `ApplicationContext has been closed already` 的级联异常，掩盖真实的配置缺失或校验失败原因（如 `@Value` 注入为空或占位值）。正确做法是抛出 `IllegalStateException`，使 Spring 干净终止并输出清晰的原始错误信息。（来源：Spring Boot 日志分析）
