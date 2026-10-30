---
title: "若依管理系统Java技术栈"
usage_scenario: []
keywords:
    - "Java 1.8"
    - "Spring Boot 2.5.15"
    - "Druid"
    - "PageHelper"
    - "Swagger3"
---

**开发语言与基础**：Java 1.8

**核心框架**：Spring Boot 2.5.15

**关键依赖**：
- `druid-spring-boot-starter 1.2.16`: 数据库连接池
- `pagehelper-spring-boot-starter 1.4.6`: 分页插件
- `springfox-boot-starter 3.0.0`: Swagger3接口文档
- `fastjson2 2.0.39`: JSON解析
- `hutool-all 5.8.26`: 通用工具包
- `kaptcha 2.3.3`: 验证码

**模块结构**：`ruoyi-admin`（后台入口）、`ruoyi-framework`（核心框架）、`ruoyi-system`（系统模块）、`ruoyi-quartz`（定时任务）、`ruoyi-generator`（代码生成）、`ruoyi-common`（通用工具）
