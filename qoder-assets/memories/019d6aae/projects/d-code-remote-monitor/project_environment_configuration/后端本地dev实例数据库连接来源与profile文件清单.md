---
title: "后端本地dev实例数据库连接来源与profile文件清单"
usage_scenario:
    - "排查本地实例连的是哪个数据库"
    - "确认后端启动读取的profile与配置文件"
    - "判断仓库内有哪些yaml配置变体"
keywords:
    - "application-dev.yaml"
    - "192.168.250.160:3306"
    - "anwei_fire_remote_monitor库"
    - "无application-local.yaml"
    - "MySQL 8.0.x服务端"
source: "auto"
---

后端本地实例（IDEA 启动 server 模块）使用 profile=dev，数据库连接来自 `server/src/main/resources/application-dev.yaml`，指向 `jdbc:mysql://192.168.250.160:3306/anwei_fire_remote_monitor`（root 账号，本机可达，MySQL 服务端为 8.0.x）。仓库另有 application-prod.yaml 与 application.yaml，**不存在 application-local.yaml**。

注：本工作区消防业务库能否经 MySQL MCP 直查、以及各 MCP 实例的服务名与读写权限现状，以「消防业务库 MySQL MCP 实例连接现状与服务名漂移判定」为准（该条实测 anwei_fire_remote_monitor_test 已放行 SELECT/INSERT/UPDATE/DELETE/DDL）。动手前一律先 `SELECT DATABASE()` 与连接主机确认实际落到哪套环境，再决定走 MCP 还是 CLI。
