---
title: "FirePropertyDO contactPhone 字段修复"
usage_scenario:
    - "修复Java实体类字段名与数据库列名不一致导致的编译错误"
    - "当VO层有contactPhone但DO层缺失时定位并修复"
keywords:
    - "FirePropertyDO"
    - "contactPhone"
    - "字段修复"
    - "编译错误"
source: "auto"
---

## Task description
- Core requirement: verify whether the contactPhone field exists in the codebase and fix any damage
- Task background: user asked "contactPhone存在这个字段吗"; investigation revealed FirePropertyDO.java has a damaged `private String c;` field (comment says "联系电话") but is referenced as `getContactPhone()` in multiple places; this causes compilation failure

## Execution process
1. Searched for contactPhone across mobile and backend codebases, confirming its presence in VO/DO layers except FirePropertyDO
2. Read FirePropertyDO.java and confirmed line 81 has `private String c;` instead of `contactPhone`
3. Verified database schema: fire_property.contact_phone column exists and contains real data
4. Executed SearchReplace to restore `c` → `contactPhone` in FirePropertyDO.java

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\dal\dataobject\firewatch\FirePropertyDO.java

## Notes
- None

## Task overview
Successfully repaired FirePropertyDO's contactPhone field: restored `private String c;` to `private String contactPhone;`, aligning Java code with database column and resolving compilation errors across 6+ calling sites.
