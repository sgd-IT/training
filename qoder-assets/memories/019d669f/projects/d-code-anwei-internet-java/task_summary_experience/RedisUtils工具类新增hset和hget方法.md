---
title: "RedisUtils工具类新增hset和hget方法"
usage_scenario:
    - "When extending RedisUtils with new hash operation methods"
    - "When implementing time-based message ID storage in Redis hashes"
keywords:
    - "RedisUtils"
    - "hset"
    - "hget"
    - "Redis hash"
source: "auto"
---

## Task description
- Core requirement: add hset and hget methods to the RedisUtils utility class to support hash-based Redis operations
- Task background: the JiagoVideoLeakageController uses Redis for audio alarm message ID matching; current usage requires storing time-based message IDs in a Redis hash (key=LYXL_FILE_MSGID, field=time, value=randomUUID) and later retrieving them; RedisUtils lacked these methods, requiring manual hashOperations calls

## Execution process
1. RedisUtils.java: add hset(String key, String hashKey, Object value, long expire) method to write to Redis hash with TTL
2. RedisUtils.java: add hget(String key, String hashKey) method to read from Redis hash
3. Verify via LSP goToDefinition that both methods resolve correctly in the controller
4. Verify via GetProblems that RedisUtils.java has no compilation errors

## Related files
- /monkey-service/src/main/java/com/monkey/general/common/utils/RedisUtils.java

## Notes
- The controller's current read path (line 232) still uses redisUtils.get(LYXL_FILE_MSGID + deviceSn), which reads a plain string key, while the new hset writes to a hash key. This mismatch needs to be addressed separately if switching to hash-based lookup.

## Task overview
Successfully added hset and hget methods to RedisUtils: the hset method supports writing to Redis hashes with TTL, and the hget method supports reading from them. Both methods are correctly resolved by the IDE and compile without errors.
