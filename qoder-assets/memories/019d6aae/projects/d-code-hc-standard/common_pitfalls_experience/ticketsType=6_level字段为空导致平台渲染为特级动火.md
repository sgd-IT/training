---
title: "ticketsType=6 level字段为空导致平台渲染为特级动火"
usage_scenario:
    - "When debugging why temporary power supply tickets appear as special-level fire operations on the platform"
    - "When implementing validation logic for work tickets"
keywords:
    - "ticketsType=6"
    - "level字段为空"
    - "平台渲染误判"
    - "dictionary mapping"
source: "auto"
---

Bug class: Platform-level rendering misinterpretation due to empty level field
Root cause: For ticketsType=6 (temporary power supply), the level field is null or empty string, and the platform's dictionary mapping treats empty values as 'special-level fire operation'
Fix pattern: Add mandatory validation for level field in BaseWorkSpecialValidator.validateBaseWorkSpecial() and fix the dictionary mapping for type=6; additionally, remove level and levelShow fields before push for ticketsType=2/6/7/8 to prevent platform misrendering.
Reusable lesson: Don't allow level=null or level="" for ticketsType=6 because it causes the platform to render as 'special-level fire operation'; instead enforce non-empty level with valid enum values. Applies when creating or modifying temporary power supply tickets; does not apply to other ticket types.
