---
title: "Ruoyi项目Redis连接需匹配application-dev.yml中ruoyi123密码及localhost:6379地址"
usage_scenario:
    - "Spring Boot应用启动报Unable to connect to Redis server"
    - "检查Redis连接失败时需核对项目配置中的密码和地址"
    - "本地调试Ruoyi框架时Redis认证失败"
keywords:
    - "Ruoyi"
    - "Redis密码"
    - "application-dev.yml"
    - "localhost:6379"
---

项目 Spring Boot 应用连接 Redis 失败的根本原因是 Redis 服务未运行或认证失败。关键配置位于 `d:\code\maintenanc\anwei-fire-maintenance\ruoyi-admin\src\main\resources\application-dev.yml` 第 140 行：`password: <REDACTED>`，且连接地址为 `localhost:6379`。若本地 Redis 未设置该密码，或监听地址/端口不符，将导致 `Unable to connect to Redis server: localhost/127.0.0.1:6379` 错误。（来源：日志分析与配置路径定位）
