---
title: "MySQL JSON列写入必须使用合法JSON字符串"
usage_scenario:
    - "When inserting data into MySQL JSON columns"
    - "When debugging 'Invalid JSON text' errors during database inserts"
    - "When extending retail store or department metadata in JSON columns (e.g., retail_config.extra_info) after data isolation refactoring"
    - "When writing to fireworks_alarm_record.extra_info in RegulatorStatisticsServiceImpl"
keywords:
    - "MySQL JSON"
    - "extra_info"
    - "JsonUtils"
    - "insertion failure"
source: "auto"
---

Bug class: MySQL JSON column insertion failure
Root cause: Writing plain strings (e.g., "当前库存: 118kg...") directly to a MySQL JSON-type column violates JSON format validation at position 0.
Fix pattern: Always wrap values in valid JSON using `JsonUtils.toJsonString(Map.of(...))` or equivalent before assigning to JSON columns.
Reusable lesson: Don't assign plain strings or unescaped text to MySQL JSON columns because it causes immediate "Invalid JSON text" errors; instead serialize the data into a valid JSON object or string. Applies when inserting into any MySQL JSON column; does not apply to VARCHAR or TEXT columns.
