---
title: "数据库连接池与Redis超时配置"
usage_scenario:
    - "排查接口响应慢或超时问题时检查连接池等待时间"
    - "优化高并发场景下的数据库连接池参数"
    - "排查Redis命令阻塞导致的接口延迟"
keywords:
    - "Druid"
    - "连接池"
    - "max-wait"
    - "Redis超时"
source: "auto"
---

项目Druid连接池配置为：max-wait 600000ms（10分钟），max-active 20，min-idle 1。Redis未配置timeout（Lettuce默认60s）。这些配置在并发高或网络抖动时会导致接口严重阻塞甚至超时。
