---
title: "SaTokenExceptionHandler must return String instead of R<Void> for NotLoginException to support SSE requests"
usage_scenario:
    - "When implementing exception handlers for SSE endpoints"
    - "When fixing HttpMediaTypeNotAcceptableException in Spring MVC"
keywords:
    - "SaTokenExceptionHandler"
    - "NotLoginException"
    - "SSE"
    - "HttpMediaTypeNotAcceptableException"
    - "content negotiation"
source: "auto"
---

Bug class: Spring content negotiation failure in exception handlers
Root cause: SaTokenExceptionHandler returns R<Void> for NotLoginException, which only Jackson can serialize (application/json); SSE requests send Accept: text/event-stream, causing no converter match → HttpMediaTypeNotAcceptableException → /error dispatch
Fix pattern: Change the handler to return String using JsonUtils.toJsonString(R.fail(...)) — StringHttpMessageConverter supports */*, so it writes for any Accept header
Reusable lesson: Don't return R objects from exception handlers that serve endpoints with non-JSON Accept headers (e.g., text/event-stream) because content negotiation will fail; instead return JSON String. Applies when handling exceptions for SSE or other non-JSON endpoints; does not apply to standard REST API endpoints that only accept application/json.
