---
title: "Special work ticket area beacon is not stored on ticket"
usage_scenario:
    - "When debugging why special work tickets show incorrect or missing area beacon values"
    - "When verifying the runtime behavior of area beacon resolution in special work ticket queries"
keywords:
    - "special work ticket"
    - "area beacon"
    - "base_area"
    - "BaseWorkSpecialServiceImpl"
source: "auto"
---

Bug class: Beacon value misattribution in special work tickets
Root cause: `area_beacon` is dynamically injected at query time via `BaseWorkSpecialServiceImpl.selectBaseWorkSpecialByOuterId()` (lines 104–109), not persisted in `base_work_special`
Fix pattern: diagnose beacon issues by tracing `base_work_special.area_id → base_area.area_beacon`, not by searching ticket fields
Reusable lesson: Don't assume `area_beacon` is stored on the ticket because it causes incorrect debugging of beacon mismatches; instead resolve it dynamically via `base_area`. Applies when troubleshooting beacon association for special work tickets; does not apply to fields actually persisted on the ticket.
