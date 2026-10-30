---
title: "PlatementValidator 动态附件校验规范"
usage_scenario:
    - "When implementing or modifying PlatementValidator validation logic"
    - "When adding a new service type to the Platement module"
keywords:
    - "PlatementValidator"
    - "dynamic validation"
    - "required attachments"
    - "service type"
source: "auto"
---

PlatementValidator must implement type-specific required attachment validation: for service type '0' (engineering), construction plans and emergency plans are required; for type '1' (service), service contracts and service reports are required; for type '2' (maintenance), repair contracts and repair records are required. This rule ensures data integrity before platform submission.
