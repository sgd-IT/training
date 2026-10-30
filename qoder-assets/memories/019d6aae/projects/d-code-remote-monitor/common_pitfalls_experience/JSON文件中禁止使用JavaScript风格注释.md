---
title: "JSON文件中禁止使用JavaScript风格注释"
usage_scenario:
    - "When editing JSON configuration files"
    - "When diagnosing build failures caused by invalid JSON syntax"
keywords:
    - "JSON"
    - "comments"
    - "parsing error"
    - "build failure"
source: "auto"
---

Bug class: JSON syntax error causing build failure
Root cause: JSON does not support JavaScript-style // comments; project.config.json contained such comments which broke the JSON parser.
Fix pattern: Remove all // comments from JSON files before saving.
Reusable lesson: Don't add JavaScript-style // comments to JSON files because they cause parsing failures and break compilation; instead use JSON schema or external documentation for notes. Applies when editing any JSON configuration file (e.g., project.config.json, package.json); does not apply to JavaScript/TypeScript files.
