---
title: "Redis WebSocket消息消费者缺失租户上下文导致广播"
usage_scenario:
    - "当部署多租户WebSocket服务使用Redis sender时"
    - "当诊断WebSocket消息被错误广播给所有租户时"
keywords:
    - "RedisWebSocketMessageConsumer"
    - "tenant context"
    - "broadcast"
    - "WebSocket sender"
source: "auto"
---

Bug class: Tenant context loss in Redis WebSocket message consumption
Root cause: RedisWebSocketMessageConsumer runs without tenant context injection, causing tenant filtering logic to degrade to broadcast.
Fix pattern: Inject tenant context (e.g., via TenantContextHolder.set()) before calling WebSocketMessageSender.send() in the consumer.
Reusable lesson: Don't run RedisWebSocketMessageConsumer without tenant context because it causes broadcast instead of per-tenant filtering; instead inject tenant context before sending. Applies when deploying multi-tenant WebSocket services using Redis sender mode; does not apply to local or non-tenant-aware sender modes.
