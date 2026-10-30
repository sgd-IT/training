---
title: "Diagnosis of AsyncRequestNotUsableException in API logs"
usage_scenario:
    - "When diagnosing AsyncRequestNotUsableException in production logs"
    - "When distinguishing between client-side and server-side causes of Broken pipe exceptions"
keywords:
    - "AsyncRequestNotUsableException"
    - "Broken pipe"
    - "API error log"
    - "client disconnect"
    - "diagnosis"
source: "auto"
---

## Task description
- Core requirement: diagnose the root cause of AsyncRequestNotUsableException appearing in the API error log page
- Task background: the exception message 'ServletOutputStream failed to write: java.io.IOException: Broken pipe' was observed in three log entries (1748/1749/1750); the goal is to determine if this is a backend bug, a frontend issue, or an infrastructure problem

## Execution process
1. Performed Grep searches across the codebase to locate relevant controller and service implementations for the affected endpoints (/dashboard/statistics and /dashboard/realtime-alarms)
2. Read FireDashboardServiceImpl.buildStatistics to understand the query logic and potential bottlenecks
3. Used the production MySQL MCP to query infra_api_access_log and infra_api_error_log tables to retrieve duration, user_ip, and result_code for the failing requests
4. Analyzed the database query results to identify that all slow requests originated from a single IP address (219.131.130.21), while other clients had normal response times
5. Confirmed the root cause as client-side disconnection (e.g., browser tab refresh, computer sleep) by finding a record with a 2-hour duration that successfully returned HTTP 200, proving the service completed but the network connection was broken

## Related files

## Notes
- None

## Task overview
Successfully diagnosed the root cause of AsyncRequestNotUsableException as client-side disconnection, not a backend bug. Evidence includes: all failing requests came from a single IP, successful long-running requests proved the service could complete, and the stack trace confirmed the failure occurred during response writing, not business logic execution.

## 备注（后续轮次补记）
- 本条结论"纯属客户端断开"已被后续排查修正：客户端断开只是最后一步，长耗时可能同时由服务端并发贡献（dashboard/statistics 单次串行十几条聚合查询、每个子查询各等一次连接池 Hikari max-wait=600000，缓存未命中惊群重算，累加可达数千秒，与 avg 261s / max 8034s 吻合）。定责完整口径与慢请求判定顺序以 common_pitfalls_experience 中的 f6367e9f（Broken pipe不可全归客户端：需并查连接池等待与串行查询）及 656f35e4（访问日志duration不能当SQL耗时致线上性能误定责）为权威源，本条仅保留早期取证快照价值，引用时须先看上述两条的边界补充。
