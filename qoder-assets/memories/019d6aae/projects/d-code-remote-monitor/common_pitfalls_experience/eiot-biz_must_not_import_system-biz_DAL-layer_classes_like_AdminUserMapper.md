---
title: "eiot-biz must not import system-biz DAL-layer classes like AdminUserMapper"
usage_scenario:
    - "When implementing tenant-scoped user queries in eiot-biz"
    - "When diagnosing unused-field warnings in eiot-biz classes"
keywords:
    - "cross-module dependency"
    - "AdminUserMapper"
    - "eiot-biz"
    - "DAL layer"
    - "API layer"
source: "auto"
---

Bug class: Cross-module dependency violation
Root cause: eiot-biz module importing system-biz's AdminUserMapper (a DAL-layer interface) creates illegal compile-time coupling between business modules; the field was injected but never used, exposing both architectural debt and unused dependency risk.
Fix pattern: Remove the unused AdminUserMapper field and its @Autowired/@Resource annotation from eiot-biz classes; use only AdminUserApi (API layer) for user queries.
Reusable lesson: Don't import or inject DAL-layer interfaces from other modules because it breaks the clean separation of concerns and introduces uncontrolled compile dependencies; instead use the published API layer. Applies when implementing user-related logic in eiot-biz; does not apply to system-biz internal implementations.
