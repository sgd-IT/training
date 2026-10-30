---
title: "Retail data scope validation must allow super-admins by default"
usage_scenario:
    - "When diagnosing data scope or permission validation failures in retail module"
    - "When implementing or modifying data scope validation logic in retail module services"
keywords:
    - "validateRetailAccess"
    - "super-admin"
    - "getAccessibleRetailIds"
    - "data scope validation"
source: "auto"
---

Bug class: Data scope validation logic inconsistency across service implementations
Root cause: InventoryRecordServiceImpl's private validateRetailAccess method performed a null check on getAccessibleRetailIds() result and threw ServiceException when null (super-admin), while AlarmRecordServiceImpl and RiskWarningRecordServiceImpl correctly allowed null; the root cause is that RetailDataScopeHelper.getAccessibleRetailIds() returns null for super-admins as its documented contract.
Fix pattern: consolidate all validateRetailAccess logic into a single public static method in RetailDataScopeHelper that checks for non-null list containment only, allowing null (super-admin) by default.
Reusable lesson: Don't perform a null check and throw on getAccessibleRetailIds() return value because it breaks the documented contract that null means 'no restriction' for super-admins; instead allow null and only check containment for non-null lists. Applies when implementing data scope validation logic using RetailDataScopeHelper.getAccessibleRetailIds(); does not apply to other helper methods with different null semantics.
