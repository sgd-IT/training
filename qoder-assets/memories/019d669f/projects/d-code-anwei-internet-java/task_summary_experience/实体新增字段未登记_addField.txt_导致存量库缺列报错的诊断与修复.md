---
title: "实体新增字段未登记 addField.txt 导致存量库缺列报错的诊断与修复"
usage_scenario:
    - "Diagnosing MyBatis-Plus Unknown column errors in production"
    - "Applying schema migrations via the project's built-in TableConfiguration mechanism"
    - "Fixing schema drift between entity definitions and database tables"
keywords:
    - "Unknown column"
    - "addField.txt"
    - "TableConfiguration"
    - "schema migration"
    - "entity field"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix two consecutive 'Unknown column' SQL errors — first on mon_alarm_information.upload_id, then on tb_casting_well_info.is_demo
- Task background: both errors stem from the same root cause — entity fields were added in commits (d146568 for upload_id, a7a3326 for is_demo) but not registered in the project's automatic migration mechanism (addField.txt), causing startup-time schema drift in customer deployments

## Execution process
1. For mon_alarm_information.upload_id: confirmed Tencent prod DB has the column, proving it's a customer DB schema drift; traced commit d146568 to verify entity addition; identified missing registration in addField.txt and template mon_alarm_information.txt
2. For tb_casting_well_info.is_demo: confirmed same pattern — entity field added in commit a7a3326 but missing in addField.txt and template tb_casting_well_info.txt
3. Fixed both by appending 4 ALTER lines for mon_alarm_information and 1 ALTER line for tb_casting_well_info to addField.txt, and updated both templates to include the new columns
4. Provided immediate manual SQL for customers to run directly on their DBs

## Related files
- /monkey-monitor-api/src/main/resources/template/master/addField.txt
- /monkey-monitor-api/src/main/resources/template/master/mon_alarm_information.txt
- /monkey-monitor-api/src/main/resources/template/master/tb_casting_well_info.txt

## Notes
- The same defect pattern occurred twice, indicating a systemic gap in the team's migration discipline; all future entity field additions must be accompanied by addField.txt registration and template updates

## Task overview
Successfully diagnosed and fixed two consecutive 'Unknown column' errors by identifying the shared root cause (missing addField.txt registration) and applying a durable, automated fix (updating addField.txt and templates); restart of monkey-monitor-api now auto-migrates missing columns for all affected tables.
