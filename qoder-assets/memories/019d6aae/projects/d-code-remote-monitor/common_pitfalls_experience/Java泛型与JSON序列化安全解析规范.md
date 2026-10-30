---
title: "Java泛型与JSON序列化安全解析规范"
usage_scenario:
    - "When parsing JSON arrays into typed collections using JsonUtils"
    - "When handling potential null values in JSON serialization"
keywords:
    - "JsonUtils"
    - "TypeReference"
    - "Jackson"
    - "LocalDate"
    - "泛型解析"
    - "空值处理"
    - "安全解析"
    - "数组格式"
    - "类型约束"
source: "auto"
---

Bug class: JsonUtils method signature misuse
Root cause: Using JsonUtils.parseArray(json, Map.class) without proper type safety causes unchecked warnings; JsonUtils.toJsonString(raw) was used but raw object may not be serializable.
Fix pattern: Use JsonUtils.parseArray(json, new TypeReference<List<Map<String, Object>>>() {}) for safe generic parsing; use JsonUtils.toJsonString(Objects.requireNonNull(raw)) to handle null safely.
Reusable lesson: Don't use raw Map.class in JsonUtils.parseArray because it loses generic type information and causes unchecked warnings; instead use TypeReference for safe generic parsing. Applies when parsing JSON arrays into typed collections; does not apply to simple string-to-object conversion.

Bug class: Jackson JSON serialization of LocalDate to array format
Root cause: Jackson's default serialization for java.time.LocalDate produces an array [year, month, day], which Vue template cannot render directly as a date string.
Fix pattern: Add a frontend formatPlanDate helper function that detects array/object/string formats and converts them to 'YYYY-MM-DD' string.
Reusable lesson: Don't render Jackson-serialized LocalDate objects directly in Vue templates because they appear as arrays; instead use a format function to normalize to string. Applies when consuming Java LocalDate from Spring Boot REST APIs; does not apply to LocalDateTime or other temporal types.

Bug class: Java generic type inference conflict
Root cause: Passing Map.class (a raw type Class<Map>) to JsonUtils.parseArray(String, Class<T>) constrains the generic type T to be both Map<String, Object> (from target assignment) and raw Map (from Map.class), causing javac to report incompatible constraints.
Fix pattern: Use an intermediate raw List to receive the result, then cast through List<?> to List<Map<String, Object>> with @SuppressWarnings("unchecked") to resolve the generic type mismatch.
Reusable lesson: Don't pass Map.class to generic parse methods because it creates conflicting type constraints on the generic parameter; instead use an intermediate raw List cast. Applies when parsing JSON arrays to typed collections with JsonUtils.parseArray; does not apply to non-generic JSON parsing methods.
