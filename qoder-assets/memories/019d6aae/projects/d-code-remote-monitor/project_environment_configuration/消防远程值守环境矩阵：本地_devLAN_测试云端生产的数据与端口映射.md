---
title: "消防远程值守环境矩阵：本地 dev/LAN 测试/云端生产的数据与端口映射"
usage_scenario:
    - "排查线上问题时先判定用户所在环境"
    - "区分测试库与生产库数据现状"
    - "核对生产库实例地址与数据新鲜度"
keywords:
    - "环境矩阵"
    - "生产库迁移"
    - "腾讯云 gz-cdb-dnqlqqwx"
    - "60477"
    - "anwei_fire_remote_monitor"
source: "user"
---

本地 dev（vite 端口 3000）经 .env.dev 连 LAN 后端 http://192.168.250.144:48080，数据库为 192.168.250.160:3306 的 anwei_fire_remote_monitor（测试环境，告警活跃）。云端 fire-remote-admin.anxunwang.cn:18080 = docker-compose 前端 nginx 容器（18080:8888，证书 anxunwang.cn），/admin-api、/app-api 反代 http://server:48080（后端容器端口未映射宿主机）。生产库自 2026-10-19 起指向腾讯云 gz-cdb-dnqlqqwx.sql.tencentcdb.com:60477 的 anwei_fire_remote_monitor（111 张表，最新告警 2026-10-19 14:23，数据活跃；MCP 实例 anwei-fire-remote-monitor-prod 已改连此实例，仅查询）；旧地址 gz-cdb-nmt6uclj.sql.tencentcdb.com:26671 上的同名库数据停在 2026-09-02，已弃用（该旧实例默认库为 anwei_iot_db）。旧实例观察：其 eiot_device_info 无 space_id 列，LAN 库有。判断用户在哪套环境复现问题时，先比对两库最新告警时间。
