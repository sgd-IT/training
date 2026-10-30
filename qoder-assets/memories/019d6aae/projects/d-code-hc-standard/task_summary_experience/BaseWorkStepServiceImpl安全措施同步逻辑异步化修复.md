---
title: "BaseWorkStepServiceImpl安全措施同步逻辑异步化修复"
usage_scenario:
    - "当排查/修复安全措施同步失败或超时问题时"
    - "当审查BaseWorkStepServiceImpl中平台API调用的线程安全与异常处理逻辑时"
    - "当为类似批量操作接口设计健壮异步推送方案时"
keywords:
    - "BaseWorkStepServiceImpl"
    - "安全措施"
    - "异步推送"
    - "ClientAbortException"
    - "并发安全"
source: "auto"
---

## Task description
- Core requirement: Fix the incorrect asynchronous platform API call logic in `BaseWorkStepServiceImpl.updateWorkStepList()` that causes excessive API calls and concurrency issues
- Task background: Original code created one thread per safety measure inside the loop, leading to N² API calls and shared mutable list access; this caused `ClientAbortException` due to timeout and potential `ConcurrentModificationException`; correct behavior requires single async call after all DB updates complete

## Execution process
1. Identified flawed pattern: `for (...) { new Thread(...).start(); }` → 16 threads → 136 total API calls
2. Diagnosed concurrency risk: multiple threads reading/writing shared `baseWorkSteps` ArrayList without synchronization
3. Fixed async placement: moved `new Thread(...)` outside the for loop to execute only once after DB updates
4. Fixed data safety: used `new ArrayList<>(baseWorkSteps)` to copy list before passing to async thread
5. Added robust error handling: try-catch + `log.warn()` in async thread
6. Improved observability: named thread `"step-sync-" + workId`

## Related files
- d:/code/hc-standard/ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkStepServiceImpl.java

## Notes
- None

## Task overview
Successfully repaired the `updateWorkStepList` method: eliminated N² API calls by moving async thread outside the loop; prevented concurrency hazards via list copy; added proper exception logging; improved thread naming for debugging — resolving both timeout-induced `ClientAbortException` and potential data inconsistency.
