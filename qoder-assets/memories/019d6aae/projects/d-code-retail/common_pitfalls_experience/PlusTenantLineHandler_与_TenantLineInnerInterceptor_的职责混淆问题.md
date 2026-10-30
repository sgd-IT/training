---
title: "PlusTenantLineHandler 与 TenantLineInnerInterceptor 的职责混淆问题"
usage_scenario:
    - "When explaining the multi-tenancy SQL injection mechanism to new team members"
    - "When debugging unexpected SQL behavior related to tenant filtering"
keywords:
    - "PlusTenantLineHandler"
    - "TenantLineInnerInterceptor"
    - "JSqlParser"
    - "SQL concatenation"
source: "auto"
---

Bug class: Misunderstanding of MyBatis-Plus multi-tenancy component roles
Root cause: The PlusTenantLineHandler interface only provides answers to three questions (ignoreTable, getTenantIdColumn, getTenantId); it does not parse or modify SQL. The actual SQL concatenation is performed by TenantLineInnerInterceptor using JSqlParser to parse the AST and inject the tenant condition.
Fix pattern: When explaining or debugging SQL concatenation, first identify which component (handler vs interceptor) is responsible for which part of the flow.
Reusable lesson: Don't assume PlusTenantLineHandler performs SQL concatenation because it is named 'LineHandler'; instead, understand that it is a callback-based strategy interface and the real AST rewriting is done by TenantLineInnerInterceptor + JSqlParser. Applies when diagnosing tenant-related SQL behavior or explaining the multi-tenancy mechanism; does not apply to direct usage of JSqlParser for custom SQL parsing.
