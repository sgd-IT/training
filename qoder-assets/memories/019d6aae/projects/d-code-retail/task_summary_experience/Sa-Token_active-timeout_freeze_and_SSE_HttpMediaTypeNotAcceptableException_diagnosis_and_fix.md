---
title: "Sa-Token active-timeout freeze and SSE HttpMediaTypeNotAcceptableException diagnosis and fix"
usage_scenario:
    - "When diagnosing SSE connection failures in Sa-Token based systems"
    - "When fixing HttpMediaTypeNotAcceptableException in Spring exception handlers"
keywords:
    - "Sa-Token"
    - "active-timeout"
    - "SSE"
    - "HttpMediaTypeNotAcceptableException"
    - "exception handler"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the log noise caused by Sa-Token token freeze and SSE request media-type negotiation failure
- Task background: frontend uses EventSource to connect to /resource/message for push notifications; when user is idle > sys_client.active_timeout (default 1800s), Sa-Token freezes the token; subsequent SSE reconnect attempts with Accept: text/event-stream fail because SaTokenExceptionHandler returns R<Void> which only Jackson can serialize (application/json), causing HttpMediaTypeNotAcceptableException → /error dispatch → double stack trace

## Execution process
1. Analyzed the log: identified 'token 已被冻结' as Sa-Token's active-timeout freeze mechanism and 'HttpMediaTypeNotAcceptableException' as the root cause of the cascade
2. Searched codebase for active-timeout config and related files: found sa-token config in application.yml, SecurityConfig.java, SaTokenExceptionHandler.java, and SseController.java
3. Read relevant files: confirmed /resource/message is handled by SseController, SecurityConfig intercepts all requests, and SaTokenExceptionHandler returns R<Void>
4. Verified the fix approach: compared with GlobalExceptionHandler's SseException handler which returns String (compatible with */*), confirming the same pattern should be applied to NotLoginException
5. Identified dependency gap: ruoyi-common-satoken module lacks ruoyi-common-json dependency needed for JsonUtils
6. Proposed code changes: add ruoyi-common-json dependency in pom.xml and modify SaTokenExceptionHandler to return String instead of R<Void>

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-satoken\pom.xml
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-satoken\src\main\java\org\dromara\common\satoken\handler\SaTokenExceptionHandler.java

## Notes
- None

## Task overview
Successfully diagnosed the root cause of the SSE exception cascade: Sa-Token's active-timeout freeze is expected behavior, but the exception handler's R<Void> return type fails content negotiation for SSE requests (Accept: text/event-stream). The fix is to return JSON String instead, matching the existing pattern in GlobalExceptionHandler for SseException. Dependency and code changes were proposed to implement this fix.
