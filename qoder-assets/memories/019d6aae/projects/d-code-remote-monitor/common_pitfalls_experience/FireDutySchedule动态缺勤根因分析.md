---
title: "FireDutySchedule动态缺勤根因分析"
usage_scenario:
    - "排查'缺勤'统计为0的问题时快速定位根因"
    - "为其他模块实现类似的时间条件动态统计功能"
keywords:
    - "FireDutySchedule"
    - "dynamic absent"
    - "root cause"
    - "time-based logic"
source: "auto"
---

## Phenomenon
'缺勤'统计卡片始终显示为0，即使存在排班结束时间已过但未签到的记录。

## Verified Root Cause
后端 `countByStatus` SQL 只按数据库 `status` 字段分组统计，不会动态计算缺勤（即排班结束时间已过但未签到的记录）。因此，除非有定时任务将超时记录标记为 `absent`，否则统计值永远为0。

## Correct Handling
1. 在 `FireDutyScheduleMapper.java` 中新增 `countDynamicAbsent` 方法，SQL逻辑为：`WHERE status = 'scheduled' AND check_in_time IS NULL AND CONCAT(schedule_date, ' ', end_time) < NOW()`
2. 在 `FireDutyScheduleServiceImpl.getScheduleStatistics()` 中调用该方法，并用其结果覆盖返回状态映射中的 `absent` 值。
3. 确保租户隔离：复用现有 `@TenantIgnore` 注解和 `tenantAware()` 包装器。

## Applicable Boundary
- 适用于所有需要基于时间条件动态计算状态的场景（如自动签到、自动签退、超时标记）。
- 不适用于仅依赖数据库静态字段的状态统计场景。
