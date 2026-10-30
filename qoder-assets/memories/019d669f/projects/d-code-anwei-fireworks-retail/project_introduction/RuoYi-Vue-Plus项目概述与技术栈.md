---
title: "RuoYi-Vue-Plus项目概述与技术栈"
usage_scenario:
    - "了解项目的整体定位、核心功能和技术选型"
keywords:
    - "RuoYi-Vue-Plus"
    - "Spring Boot 4.1"
    - "Vue3"
    - "Sa-Token"
    - "SnailJob"
source: "init"
---

项目定位：Dromara RuoYi-Vue-Plus 6.0.0-BETA，针对分布式集群场景全方位升级的重写版RuoYi框架。

核心特性：
- 前端：Vue3 + TypeScript + ElementPlus
- 后端：Spring Boot 4.1.0, JDK 21/25
- 容器：Jetty (基于Netty)
- 认证：Sa-Token + JWT
- ORM：MyBatis-Plus + Dynamic-Datasource
- 缓存：Redisson (支持Redis 6+)
- 任务调度：SnailJob (分布式)
- 文件存储：Minio/RustFS/AWS S3
- 工作流：WarmFlow
- 监控：SpringBoot-Admin + SkyWalking
- 代码生成：支持动态多数据源

架构优势：插件化扩展、异构数据库支持、分布式锁、接口加密、数据脱敏、SSE推送等。
