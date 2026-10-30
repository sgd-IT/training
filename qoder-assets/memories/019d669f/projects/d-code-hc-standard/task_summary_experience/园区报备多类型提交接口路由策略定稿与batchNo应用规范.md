---
title: "园区报备多类型提交接口路由策略定稿与batchNo应用规范"
usage_scenario:
    - "When implementing multi-type park report submission"
    - "When designing frontend-backend interface routing for type-count branching"
keywords:
    - "园区报备"
    - "multi-type"
    - "interface routing"
    - "batchNo"
    - "outerId"
source: "auto"
---

## Task description
- Core requirement: finalize the interface routing strategy for multi-type park report submission (e.g., user selects ≥2 types)
- Task background: park's PlatementPrepareApi.save() accepts an array; outerId is immutable primary key; ticket prepareId must reference it; two implementation forms exist (type multi-value vs batch submission); Route A (local split + batchNo) was adopted over Form 2 (derived outerId + mapping)

## Execution process
1. Verified frontend type-count branching logic: if typeLevels.length > 1 → call new batch interface; else → use existing single interface
2. Backend added two new controllers: POST /base/TicketBb/batch (PC) and POST /api/ticketBase/batchAddTicketBb (App)
3. Both new controllers delegate to unified service.batchInsertBaseTicketBb()
4. Confirmed outerId generation via IdUtils.fastSimpleUUID() ensures safe local split without object reuse
5. Confirmed batchNo is local-only and must be stripped before serialization to park API
6. Finalized failure behavior: stop on first failure, return partial success list + error

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseTicketBbServiceImpl.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementPrepareApi.java

## Notes
- Reusing the same BaseTicketBb object instance in a loop causes all reports to share one outerId → breaks park data integrity; each report must be a new object
- batchNo field must be removed from PlatementPrepareApi.save() payload before serialization to avoid park rejection

## Task overview
Successfully finalized the multi-type park report interface routing strategy: frontend dispatches by type count; backend adds two new batch endpoints delegating to a shared service method; outerId generation and batchNo handling rules are confirmed and codified.
