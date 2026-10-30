---
title: "贵州数据推送access_status状态重置（人员+车辆）"
usage_scenario:
    - "When resetting push eligibility state for multiple entity types in GZ data push module"
    - "When validating that database state aligns with configured access_status semantics"
keywords:
    - "贵州推送"
    - "access_status"
    - "状态重置"
    - "人员出入"
    - "车辆出入"
source: "auto"
---

## Task description
- Core requirement: reset access_status=0 for all person and car data created after July 8th to ensure consistent push eligibility
- Task background: previous manual or partial state adjustments led to inconsistent access_status values; automated, environment-safe, parameterized reset is needed to guarantee clean push state across both entity types

## Execution process
1. Query em_person_in_out_info and em_car_in_out_info to confirm current access_status distribution for records with company_code='5202814103' and in_out_time >= '2026-07-08'
2. Execute UPDATE on em_person_in_out_info SET access_status=0 WHERE company_code='5202814103' AND in_out_time >= '2026-07-08'
3. Execute UPDATE on em_car_in_out_info SET access_status=0 WHERE company_code='5202814103' AND in_out_time >= '2026-07-08'
4. Verify final state via SELECT confirming all targeted rows are now access_status=0

## Related files
- None (database-only operation)

## Notes
- None

## Task overview
Successfully reset access_status=0 for all person and car entries created after July 8th: verified initial state, applied two targeted UPDATEs, and confirmed final state with SELECT — ensuring both tables are ready for consistent push eligibility.
