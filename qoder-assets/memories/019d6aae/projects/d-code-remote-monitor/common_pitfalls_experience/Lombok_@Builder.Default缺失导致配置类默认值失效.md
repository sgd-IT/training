---
title: "Lombok @Builder.Default缺失导致配置类默认值失效"
usage_scenario:
    - "When defining configuration classes with Lombok @Builder"
    - "When debugging null field values in builder-constructed objects"
keywords:
    - "@Builder"
    - "@Builder.Default"
    - "Lombok"
    - "configuration class"
    - "default value"
source: "auto"
---

Bug class: Lombok builder initialization issue
Root cause: @Builder annotation ignores field initialization expressions (e.g., = false) unless @Builder.Default is explicitly added; this causes the built object's field to be null instead of the intended default value.
Fix pattern: Add @Builder.Default to any field that has an initialization expression and should use that value as the default when not set during builder construction.
Reusable lesson: Don't omit @Builder.Default on fields with initialization expressions because the builder will ignore the default value and set it to null; instead add @Builder.Default. Applies when using @Builder on configuration classes with default field values; does not apply to fields that should remain unsettable or are final.
