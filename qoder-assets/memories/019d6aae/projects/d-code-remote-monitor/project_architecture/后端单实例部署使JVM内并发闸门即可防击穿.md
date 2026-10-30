---
title: "后端单实例部署使JVM内并发闸门即可防击穿"
usage_scenario:
    - "为接口选择防击穿或并发限流方案时"
    - "评估是否需要引入分布式锁能力时"
    - "确认后端部署副本数量与反代结构时"
    - "新增定时任务时判断用Spring原生@Scheduled还是Quartz JobHandler"
keywords:
    - "单实例容器部署"
    - "nginx单upstream"
    - "JVM内并发闸门"
    - "docker-compose server副本"
source: "auto"
---

anwei-fire-remote-monitor 后端生产部署为单实例：deploy/docker-compose.yml 只有一个 server 容器（container_name: anwei-remote-monitor），deploy/config/frontend/nginx.conf 的 proxy_pass 指向 http://server:48080 单地址、无 upstream 多副本列表。因此接口内的"防击穿/并发上限/单飞重建"这类闸门用 JVM 内原语（如 Semaphore.tryAcquire）即可覆盖全部流量，引入 RedissonClient 分布式锁或 RRateLimiter 属于过度设计。跨实例一致性只在扩容为多副本后才需要重新评估。判据来源是实际读取 nginx.conf 与 docker-compose.yml，而非口头约定。
