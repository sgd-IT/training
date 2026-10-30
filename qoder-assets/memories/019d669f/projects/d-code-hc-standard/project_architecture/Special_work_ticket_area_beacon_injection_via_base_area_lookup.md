---
title: "Special work ticket area beacon injection via base_area lookup"
usage_scenario:
    - "When modifying how area beacon values are resolved or assigned to special work tickets"
    - "When debugging mismatched or missing area beacon values in special work ticket queries"
keywords:
    - "special work ticket"
    - "area beacon"
    - "base_area"
    - "BaseWorkSpecialServiceImpl"
source: "auto"
---

The `area_beacon` field of special work tickets is not persisted in `base_work_special`, but is dynamically injected during query execution by joining with `base_area` and assigning `baseArea.getAreaBeacon()` to the ticket object.
