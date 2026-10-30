---
title: "RuoYi-Plus createBy 字段类型不匹配导致权限检查失败"
usage_scenario:
    - "When diagnosing data scope or permission validation failures in retail module"
    - "When implementing or modifying data scope validation logic in retail module services"
keywords:
    - "createBy"
    - "type mismatch"
    - "permission check"
    - "LoginHelper"
    - "RuoYi-Plus"
source: "auto"
---

Bug class: 权限检查逻辑错误
Root cause: RetailInfo.createBy 字段由 LoginHelper.getUsername() 填充（String 类型），但 InventoryRecordServiceImpl.validateUserRetail() 中使用 LoginHelper.getUserId()（Long 类型）进行比较，导致 String 与 Long 的 Objects.equals 永远为 false
Fix pattern: 在 validateUserRetail() 中将 LoginHelper.getUserId() 替换为 LoginHelper.getUsername()，并添加 retailInfo == null 的空指针防护
Reusable lesson: 不要直接用 LoginHelper.getUserId() 与框架自动填充的 createBy 字段比较，因为 createBy 存储的是用户名而非用户 ID；应统一使用 LoginHelper.getUsername() 进行比较。适用于 RuoYi-Plus 项目中所有对 createBy/createdBy 字段的业务逻辑判断；不适用于自定义填充了用户 ID 的特殊场景。
