---
title: "Special work ticket area beacon resolution mechanism"
usage_scenario:
    - "When diagnosing why multiple special work tickets share the same area beacon"
    - "When verifying or modifying the runtime beacon injection logic in special work ticket queries"
keywords:
    - "special work ticket"
    - "area beacon"
    - "base_area"
    - "BaseWorkSpecialServiceImpl"
    - "setAreaBeacon"
source: "auto"
---

## Task description
- Core requirement: explain how 4 special work tickets (all with outerId starting '2010437129') are associated with the same area beacon value
- Task background: tickets do not store beacon directly; the association is resolved at query time via area_id join; understanding this chain is critical for diagnosis, configuration, and real-time updates

## Execution process
1. Confirmed tickets store only area_id (e.g., 720), not area_beacon — beacon is resolved dynamically during query execution
2. Verified backend logic in BaseWorkSpecialServiceImpl.java: lines 104–109 call baseAreaMapper.selectAreaById() and setAreaBeacon(baseArea.getAreaBeacon())
3. Confirmed APP uses JobSlipBean.areaBeacon for Bluetooth device matching
4. Provided executable SQL to join base_work_special and base_area and filter by area_beacon = '2010437129'
5. Derived three key operational conclusions: (a) changing area_beacon affects all tickets sharing that area_id instantly; (b) changes take effect on next ticket load (no data migration needed); (c) beacon mismatch diagnosis requires checking only area_id → base_area.area_beacon → physical device broadcast

## Related files
None

## Notes
None

## Task overview
Successfully explained and verified the area beacon resolution mechanism: clarified that beacon association is dynamic and centralized at the base_area level; provided production-ready SQL and actionable operational insights; no code/config changes were required as this was a diagnostic clarification task.
