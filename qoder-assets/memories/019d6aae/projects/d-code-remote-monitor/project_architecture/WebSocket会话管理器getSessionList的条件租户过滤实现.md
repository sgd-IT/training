---
title: "WebSocket会话管理器getSessionList的条件租户过滤实现"
usage_scenario:
    - "为多租户WebSocket服务添加定向推送功能时"
    - "诊断WebSocket消息被错误广播给所有租户时"
keywords:
    - "WebSocketSessionManagerImpl"
    - "getSessionList"
    - "TenantContextHolder"
    - "会话过滤"
    - "广播"
source: "auto"
---

WebSocketSessionManagerImpl.getSessionList 方法中实现了条件性的租户过滤逻辑：仅当调用线程的租户上下文（TenantContextHolder.getTenantId()）非空时，才根据该租户ID对会话列表进行过滤；若上下文为空，则返回所有租户的会话。此逻辑位于 getSessionList 方法的第125行。
