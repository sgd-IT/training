---
title: "FireDutySchedule动态缺勤统计实现"
usage_scenario:
    - "Implementing real-time absenteeism tracking in duty scheduling systems"
    - "Extending existing status-based statistics with time-condition logic"
    - "Adding dynamic business rules to static database counts"
keywords:
    - "FireDutySchedule"
    - "dynamic absent count"
    - "SQL CONCAT"
    - "tenant isolation"
    - "status override"
source: "auto"
---

## Implementation Summary
- Added new Mapper method `countDynamicAbsent` in `FireDutyScheduleMapper.java` to dynamically count records where `status = 'scheduled'`, `check_in_time IS NULL`, and `CONCAT(schedule_date, ' ', end_time) < NOW()`.
- Modified `FireDutyScheduleServiceImpl.getScheduleStatistics()` to call `countDynamicAbsent` and override the `absent` value in the returned status map.
- Ensured tenant isolation is preserved by leveraging existing `@TenantIgnore` annotations and `tenantAware()` wrapper.
- The solution handles edge cases like night shifts (end_time < start_time) and respects date range filters.

## Key Files
- `FireDutyScheduleMapper.java`: New SQL query definition
- `FireDutyScheduleServiceImpl.java`: Service logic integration

## Usage Scenario
When implementing real-time absenteeism tracking in duty scheduling systems where database status alone is insufficient, use this pattern to compute dynamic absence based on time conditions.
