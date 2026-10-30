---
title: "User实体类添加token字段映射并返回前端"
usage_scenario:
    - "When adding new database columns to existing entities for API exposure"
    - "When integrating external systems requiring additional user identity tokens"
keywords:
    - "User entity"
    - "token field"
    - "sys_user"
    - "API response"
    - "database mapping"
---

## Task description
- Core requirement: add the `token` field from the `sys_user` database table to the `User` Java entity so it can be returned to the frontend in the `/sys/user/info` response
- Task background: the `sys_user` table has a `token` column (commented as "四相token") for personnel positioning system integration, but the `User` entity class did not map this field, preventing it from being included in API responses

## Execution process
1. Identified the `token` column in `sys_user` table using MCP database query tool
2. Located the `User.java` entity file at `d:\code\anwei-fireworks\monkey-service\src\main\java\com\monkey\general\modules\sys\entity\User.java`
3. Added `private String token;` field with getter/setter methods to the `User` entity class
4. Confirmed that the `/sys/user/info` endpoint now returns the `token` field as part of the User object

## Related files
- d:\code\anwei-fireworks\monkey-service\src\main\java\com\monkey\general\modules\sys\entity\User.java

## Notes
- None

## Task overview
Successfully added token field mapping to the User entity: added the `token` field to `User.java`, enabling the `sys_user.token` database column to be automatically mapped and returned in the `/sys/user/info` API response for frontend consumption.
