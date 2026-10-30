---
title: "Avoid caching for maintenance window queries to ensure immediate popup restoration"
usage_scenario:
    - "When designing real-time feature suppression logic"
    - "When choosing between caching and direct DB lookup for time-sensitive queries"
keywords:
    - "maintenance window"
    - "caching"
    - "TTL delay"
    - "real-time suppression"
source: "auto"
---

Conclusion: Do not cache the maintenance window query result; perform direct database lookup each time.
Rationale (trade-off): Caching introduces TTL delay, which violates the requirement that popup suppression must end immediately upon plan completion; direct DB lookup with proper index is fast enough and avoids inconsistency.
Rejected alternatives: Using local or Redis cache for the maintenance window query — dropped because it adds unnecessary complexity and risks delayed popup restoration after plan completion.
Applicable / expiry conditions: Holds while popup restoration must be immediate upon plan status change; re-evaluate if query performance becomes a bottleneck under high load.
