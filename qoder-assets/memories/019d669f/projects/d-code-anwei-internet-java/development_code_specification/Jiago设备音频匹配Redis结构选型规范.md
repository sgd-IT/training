---
title: "Jiago设备音频匹配Redis结构选型规范"
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

For Jiago device audio matching, use Redis Hash structure (time as field) instead of ZSET (time as score). Rationale: Hash provides O(1) exact match when time matches perfectly; fallback to HGETALL + time-difference calculation is acceptable for the expected scale (thousands of entries); ZSET would require two range queries and more complex logic but offers no significant advantage. Applicable while device push and file upload use the same time format and precision; re-evaluate if time precision degrades or mismatch frequency increases significantly.
