---
title: "贵州省平台API存在30秒调用频率限制"
usage_scenario: []
keywords:
    - "GZ平台"
    - "接口限流"
    - "30秒周期"
    - "syncNetworkCompanyToDB"
source: "user"
---

贵州省平台接口 `/network/info/get` 有严格限流策略：30秒内仅允许一次调用。若多个定时任务或并发线程重复调用 `syncNetworkCompanyToDB()` 方法，将触发 '访问过于频繁' 错误（code=-1）。必须在调用前增加本地时间戳缓存机制，确保30秒内仅首次调用生效。（来源：日志分析）
