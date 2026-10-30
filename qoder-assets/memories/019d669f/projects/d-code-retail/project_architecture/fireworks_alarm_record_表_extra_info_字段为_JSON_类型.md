---
title: "fireworks_alarm_record 表 extra_info 字段为 JSON 类型"
usage_scenario:
    - "When adding or modifying data written to the fireworks_alarm_record table"
    - "When designing new fields for alarm records that require structured storage"
keywords:
    - "fireworks_alarm_record"
    - "extra_info"
    - "JSON column"
    - "fireworks_init.sql"
    - "retail_data_scope"
    - "permission_metadata"
    - "RegulatorStatisticsServiceImpl"
    - "alarm trend"
    - "alarm distribution"
source: "auto"
---

The `fireworks_alarm_record` table has a column `extra_info` of type JSON, defined in the database initialization script `fireworks_init.sql` (line 131). Its purpose is to store extended information such as bounding boxes or tracking IDs.
