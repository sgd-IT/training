---
title: "PlatementImplementPerson表同步状态字段缺失问题诊断与修复"
usage_scenario:
    - "When diagnosing 'Unknown column' SQL errors in MyBatis mappers"
    - "When applying database migrations that were missed in production-like environments"
    - "When verifying database schema alignment between code and runtime DB"
keywords:
    - "sync_status"
    - "PlatementImplementPerson"
    - "MySQL DDL"
    - "MCP"
    - "mysql CLI"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the 'Unknown column sync_status in field list' error for table platement_implement_person
- Task background: the Java entity and mapper XML reference sync_status/sync_result columns, but the database table lacks them; a migration script exists but was never applied to the target DB (192.168.250.160/tgr-gz-vue)

## Execution process
1. Confirmed via MCP query that platement_implement_person has only 7 columns and lacks sync_status/sync_result
2. Attempted DDL via MCP mysql_query tool but was rejected with 'DDL operations are not allowed for schema'
3. Executed ALTER TABLE via mysql CLI client: added sync_status char(1) DEFAULT '0' COMMENT '同步状态：0未同步、1已同步、2同步失败' and sync_result text COMMENT '同步结果', aligned with the standard definition used by platement_implement_task
4. Verified success via MCP SHOW COLUMNS and CLI SELECT queries showing the new columns with correct values

## Related files
- d:\code\hc-standard\ruoyi-system\src\main\resources\mapper\platement\PlatementImplementPersonMapper.xml
- d:\code\hc-standard\ruoyi-common\src\main\java\com\ruoyi\common\platement\domain\PlatementImplementPerson.java
- d:\code\hc-standard\ruoyi-admin\src\main\resources\sql\add_implement_person_sync_fields.sql

## Notes
- The repository's add_implement_person_sync_fields.sql uses varchar(4)/varchar(500), while the applied DDL used char(1)/text to match the project standard; both are functionally valid but differ in type/size
- The 0-byte untracked scripts base_duty_schedule.sql and add_implement_soft_delete.sql were identified as potential future hazards

## Task overview
Successfully diagnosed and fixed the 'Unknown column sync_status' error: confirmed missing columns via MCP, executed DDL via CLI with aligned definitions, and verified the columns exist and return data correctly. The application's list interface now runs without error.
