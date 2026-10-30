---
title: "FireMaintenancePlanServiceImpl compilation errors due to missing import and invalid builder usage"
usage_scenario:
    - "When diagnosing Java compilation failures in service layer files"
    - "When integrating custom Spring components into service logic"
keywords:
    - "FireMaintenancePlanServiceImpl"
    - "UserTenantResolver"
    - "builder method"
    - "compilation error"
source: "auto"
---

Bug class: Java compilation failure in service layer
Root cause: FireMaintenancePlanServiceImpl.java directly calls UserTenantResolver.resolveLoginUserTenantId() without importing the class and without injecting it as a Spring component; also uses invalid .targetTenantId() builder call without parameter and omits startTime field.
Fix pattern: Add @Resource injection for UserTenantResolver, pass correct parameters to builder methods, and add missing startTime field to VO/DO.
Reusable lesson: Don't call static methods on non-static Spring components without proper injection because it causes compilation failure; instead inject the component and call instance methods. Applies when using project-defined Spring services like UserTenantResolver; does not apply to utility classes with static methods.
