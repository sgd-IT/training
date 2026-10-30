---
title: "修复@PostConstruct中SpringApplication.exit()导致的上下文关闭异常"
usage_scenario:
    - "Spring Boot项目中@PostConstruct内强制退出引发Bean初始化失败的修复"
    - "排查DynamicDataSource等Bean报'has been closed already'时溯源到exit()调用"
    - "同类配置校验类（如LicenseCheck、DBConnectionCheck）避免exit()的规范实现"
keywords:
    - "@PostConstruct"
    - "SpringApplication.exit"
    - "ApplicationContext关闭"
    - "级联异常"
---

## 任务描述
修复 CompanyCodeCheckConfig 在 @PostConstruct 中调用 SpringApplication.exit() 导致 ApplicationContext 提前关闭、引发 DynamicDataSource 等 Bean 初始化失败的级联异常。

## 执行过程
```mermaid
graph TD
    A[日志发现启动失败] --> B[定位CompanyCodeCheckConfig.java]
    B --> C[分析@PostConstruct中exit()行为]
    C --> D[确认exit()导致ApplicationContext关闭]
    D --> E[移除ApplicationContext依赖及构造函数注入]
    E --> F[将SpringApplication.exit()替换为IllegalStateException抛出]
```

## 任务总结
成功修复企业编码校验逻辑：删除 ApplicationContext 注入、移除 SpringApplication.exit() 调用，改为抛出 IllegalStateException。使 Spring Boot 启动失败时能干净终止，避免 ApplicationContext 已关闭的级联异常，提升错误可读性与调试效率。
