---
title: "Scheduled task TooManyResultsException 根因分析与修复方案"
usage_scenario:
    - "当定时任务中出现TooManyResultsException时进行根因分析和修复"
    - "排查MyBatis selectOne查询返回多条记录的问题"
    - "处理数据库中重复主键或唯一字段导致的查询异常"
keywords:
    - "TooManyResultsException"
    - "selectOne"
    - "scheduled task"
    - "DeviceStateCheckTask"
    - "product_key"
    - "database duplicate"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the `TooManyResultsException` occurring in a scheduled task
- Task background: the error `org.apache.ibatis.exceptions.TooManyResultsException: Expected one result (or null) to be returned by selectOne(), but found: 2` indicates that a `selectOne()` query returned two results instead of the expected one or null; this is happening in a scheduled task context, most likely `DeviceStateCheckTask.syncState()` which runs every 10 seconds and calls `productService.getProductByPkFromCache()`

## Execution process
1. Identified the most probable culprit: `DeviceStateCheckTask.syncState()` calling `productService.getProductByPkFromCache(device.getProductKey())` → `ProductServiceImpl.getByPk(pk)` → `productMapper.selectOne(ProductDO::getProductKey, pk)`
2. Diagnosed the root cause: duplicate `product_key` values in the `eiot_product` database table
3. Proposed SQL-based cleanup: first identify duplicates with `SELECT product_key, COUNT(*) FROM eiot_product GROUP BY product_key HAVING COUNT(*) > 1`, then delete extras while preserving the record with the smallest `id`
4. Proposed code-level defense: modify `ProductServiceImpl.getByPk()` to add `.last("LIMIT 1")` to the query wrapper to prevent future exceptions
5. Also identified `ThingModelServiceImpl.getThingModelByProductKey()` as having similar risk and proposed the same `LIMIT 1` fix

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\product\ProductServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\product\ThingModelServiceImpl.java

## Notes
- Critical insight: the `@TenantIgnore` annotation on `DeviceStateCheckTask.syncState()` means tenant context is bypassed, so the `tenantAware()` wrapper around `getByPk()` may not fully mitigate the issue if duplicate data exists across tenants
- The stack trace is truncated, so while `DeviceStateCheckTask` is the most likely source due to its high frequency (every 10 seconds), other scheduled tasks like `MaintenanceUrgeTask` or `RedisPendingMessageResendJob` could also be involved

## Task overview
Successfully diagnosed the root cause of the `TooManyResultsException` as duplicate `product_key` entries in the database and provided both immediate database cleanup steps and long-term code-level defensive fixes. The solution includes precise SQL queries for identification and cleanup, as well as targeted Java code modifications to add `LIMIT 1` to vulnerable `selectOne()` calls.
