---
title: "Retail data scope validation method consolidation"
usage_scenario:
    - "When unifying duplicated validation logic across multiple service implementations"
    - "When refactoring inconsistent super-admin handling in data scope utilities"
keywords:
    - "validateRetailAccess"
    - "RetailDataScopeHelper"
    - "method consolidation"
    - "super-admin handling"
source: "auto"
---

## Task description
- Core requirement: consolidate three duplicate private validateRetailAccess methods (in InventoryRecordServiceImpl, AlarmRecordServiceImpl, and RiskWarningRecordServiceImpl) into one public static method in RetailDataScopeHelper
- Task background: each service implemented its own copy of validateRetailAccess with inconsistent behavior — InventoryRecordServiceImpl threw an exception for super-admins (due to null check), while AlarmRecordServiceImpl and RiskWarningRecordServiceImpl correctly allowed them; the goal was to unify behavior to the latter and eliminate duplication

## Execution process
1. RetailDataScopeHelper.java: add public static void validateRetailAccess(String retailId) — checks getAccessibleRetailIds() result; throws ServiceException if non-null list does not contain retailId; allows super-admins (ids == null) by default
2. InventoryRecordServiceImpl.java: delete private validateRetailAccess method and replace all 3 calls (L60, L165, L213) with RetailDataScopeHelper.validateRetailAccess(...)
3. AlarmRecordServiceImpl.java: delete private validateRetailAccess method and replace all 3 calls (L233, L258, L329) with RetailDataScopeHelper.validateRetailAccess(...)
4. RiskWarningRecordServiceImpl.java: delete private validateRetailAccess method and replace its 1 call (L124) with RetailDataScopeHelper.validateRetailAccess(...)

## Related files
- /src/main/java/org/dromara/fireworks/utils/RetailDataScopeHelper.java
- /src/main/java/org/dromara/fireworks/service/impl/InventoryRecordServiceImpl.java
- /src/main/java/org/dromara/fireworks/service/impl/AlarmRecordServiceImpl.java
- /src/main/java/org/dromara/fireworks/service/impl/RiskWarningRecordServiceImpl.java

## Notes
- None

## Task overview
Successfully consolidated the validateRetailAccess logic: added a single public static method in RetailDataScopeHelper that correctly handles super-admins (by allowing null ids); deleted all three private copies; replaced all 7 call sites; eliminated behavioral inconsistency and code duplication.
