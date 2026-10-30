---
title: "anwei-inner 主运行数据库连接配置位置"
usage_scenario:
    - "需要确认 anwei-inner 项目实际连接的数据库"
    - "修改或新增数据库相关配置与 MCP 服务"
    - "排查模块 yaml 中数据源取值不一致"
keywords:
    - "anwei-office"
    - "192.168.250.160"
    - "MySQL 连接串"
    - "application yaml"
    - "MYSQL_URL"
source: "auto"
---

anwei-inner 项目（后端 anwei-inner-office，yudao 框架）的主运行数据库为 MySQL：jdbc:mysql://192.168.250.160:3306/anwei-office，账号 root。连接串定义在 anwei-inner-office\yudao-server\src\main\resources\application-local.yaml、application-dev.yaml、application-prod.yaml 中，其中 application-prod.yaml 通过 MYSQL_URL / MYSQL_SLAVE_URL 环境变量占位但默认值同样指向 192.168.250.160/anwei-office，即开发配置与生产配置共用同一份数据。各 module-server（yudao-module-system / report / wms 等）resources 下的 yaml 多为 127.0.0.1 示例占位值，不代表实际使用库；判断实际生效库应以 yudao-server 的配置为准。
