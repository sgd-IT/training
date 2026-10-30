---
title: "RetailInfo.createBy 字段类型不匹配问题诊断与修复"
usage_scenario:
    - "当遇到权限检查失败但逻辑看似正确时，排查字段数据类型和来源"
    - "在RuoYi-Plus项目中验证框架自动填充字段（如createBy）的实际值"
keywords:
    - "createBy"
    - "type mismatch"
    - "permission check"
    - "LoginHelper"
    - "RuoYi-Plus"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the permission check bug in validateUserRetail()
- Task background: RetailInfo.createBy is populated by LoginHelper.getUsername() (String), but validateUserRetail() compares it with LoginHelper.getUserId() (Long); this type mismatch causes all non-superadmin users to be incorrectly denied access

## Execution process
1. Grep for retailInfo.getCreateBy() and createBy usage to identify where the field is set and used
2. Grep for LoginHelper.getUserId() and LoginHelper.getUsername() to confirm return types and usage patterns across the codebase
3. Identify that RetailInfoServiceImpl.insertByBo() calls add.setCreateBy(LoginHelper.getUsername()) → confirms createBy is String
4. Identify that InventoryRecordServiceImpl.validateUserRetail() compares retailInfo.getCreateBy() (String) with LoginHelper.getUserId() (Long) → confirms type mismatch bug
5. Fix: replace LoginHelper.getUserId() with LoginHelper.getUsername() in the comparison, and add null-check for retailInfo

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\RetailInfoServiceImpl.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\InventoryRecordServiceImpl.java

## Notes
- The RuoYi-Plus framework's default MetaObjectHandler fills createBy with the username, not the user ID — this is a common source of confusion when developers assume numeric IDs
- Always verify the actual data type and value source of framework-managed fields before writing business logic that depends on them

## Task overview
Successfully diagnosed and fixed the RetailInfo.createBy type mismatch bug: confirmed createBy stores username (String), not user ID (Long); updated validateUserRetail() to compare usernames instead of user IDs; added null safety for retailInfo; the fix resolves the incorrect permission denial for all non-superadmin users.
