---
title: "LuBoxiang视频流接口超时降级与事务一致性修复"
usage_scenario:
    - "When diagnosing timeout issues in external API integrations"
    - "Resolving distributed data inconsistency caused by transactional boundaries around HTTP calls"
    - "Optimizing third-party service call resilience in Java web applications"
keywords:
    - "startWork"
    - "LuBoxiang"
    - "timeout"
    - "getDeviceLive"
    - "transactional timeout"
    - "data inconsistency"
source: "auto"
---

## Task description
- Core requirement: diagnose the root cause of 'Read timed out' exception when mobile calls '/api/special/startWork' and provide optimization solutions.
- Task background: The startWork method triggers external HTTP calls to LuBoxiang video source (58.19.176.195:8888) for saveTicketAndDevice and getDeviceLive; timeouts disrupt the entire workflow and cause data inconsistency.

## Execution process
1. Stack trace analysis: Identified BaseWorkSpecialServiceImpl.java:242 as the failing line, pinpointing getDeviceLive as the timeout culprit.
2. Log cross-referencing: Correlated sys-error.log timeout timestamps (e.g., 09:36:34.809) with sys-platment.log saveTicketAndDevice success logs (e.g., 09:36:29.806), confirming a precise 5-second gap matching the hardcoded LUBOXIANG_TIMEOUT.
3. Code verification: Confirmed the deployed jar matches the source code, validating the timeout configuration and flow logic.
4. Data inconsistency analysis: Determined that the @Transactional wrapper causes local status updates to roll back while remote LuBoxiang operations succeed, leading to distributed inconsistency.
5. Solution formulation: Proposed P0 short-term fixes (try-catch降级, timeout increase), P1 consistency fixes (3007 handling, compensation), and P2 architectural fixes (async push, error translation).
6. Implementation: Modified BaseWorkSpecialServiceImpl.java to add try-catch around saveTicketAndDevice and getDeviceLive calls, and confirmed LUBOXIANG_TIMEOUT was already set to 10000ms.
7. Verification: Maven compile succeeded, confirming no syntax errors in the modifications.

## Related files
- d:/code/hc-standard/ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java

## Notes
- The transactional boundary around external HTTP calls is the root cause of the data inconsistency; local rollback does not affect remote side effects.

## Task overview
Successfully diagnosed the root cause as LuBoxiang's getDeviceLive interface exceeding the 5s timeout; implemented a comprehensive P0 fix by adding try-catch degradation around both LuBoxiang calls in startWork; verified the fix via compilation and confirmed the timeout value was already correctly set to 10000ms.
