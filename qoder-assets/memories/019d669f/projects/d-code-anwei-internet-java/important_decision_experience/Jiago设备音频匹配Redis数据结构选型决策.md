---
title: "Jiago设备音频匹配Redis数据结构选型决策"
usage_scenario:
    - "When designing Redis-based message-id storage for time-sensitive device uploads"
    - "When choosing between Redis data structures for time-based matching"
keywords:
    - "Jiago"
    - "Redis"
    - "Hash"
    - "ZSET"
    - "time matching"
source: "auto"
---

Conclusion: Use Redis Hash structure (time as field) instead of ZSET (time as score) for Jiago device audio matching.
Rationale (trade-off): Hash provides O(1) exact match when time matches perfectly, and fallback to HGETALL + time-difference calculation is acceptable for the expected scale (thousands of entries); ZSET would require two range queries and more complex logic but offers no significant advantage for this use case.
Rejected alternatives: Using ZSET with time as score — dropped because the Hash structure is simpler, meets the requirement, and avoids unnecessary complexity.
Applicable / expiry conditions: holds while the device push and file upload use the same time format and precision; re-evaluate if time precision degrades or mismatch frequency increases significantly.
