---
title: "后端端口硬编码为8088且定义在application.yml"
usage_scenario:
    - "Vite代理报ECONNREFUSED时需确认后端实际监听端口"
    - "排查前后端通信失败时快速定位后端配置端口位置"
keywords:
    - "application.yml"
    - "server.port"
    - "8088"
    - "ECONNREFUSED"
---

该项目后端 Spring Boot 服务端口在 `anwei-fire-maintenance\ruoyi-admin\src\main\resources\application.yml` 中硬编码配置为 `server.port: 8088`（第4行），而非默认8080。前端 Vite 代理 `/dev-api` 到 `http://localhost:8088` 失败（ECONNREFUSED）的根本原因是后端未启动。启动后端即可解决。（来源：Grep）
