---
title: "PlusTenantLineHandler 与 MyBatis-Plus SQL 拼接机制说明任务"
usage_scenario:
    - "When explaining the multi-tenancy SQL injection mechanism to new team members"
    - "When debugging unexpected SQL behavior related to tenant filtering"
keywords:
    - "PlusTenantLineHandler"
    - "TenantLineInnerInterceptor"
    - "MyBatis-Plus"
    - "SQL concatenation"
    - "JSqlParser"
source: "auto"
---

## Task description
- Core requirement: explain how PlusTenantLineHandler operates with MyBatis-Plus to concatenate SQL
- Task background: user asked a conceptual question about the SQL concatenation mechanism; no code change was requested, but a complete, accurate technical explanation was required and delivered

## Execution process
1. Identified the core files: PlusTenantLineHandler.java (the handler), MybatisPlusConfig.java (the interceptor registration), TenantAutoConfiguration.java (the handler bean registration), and TenantContextHolder.java (the tenant context holder)
2. Read and analyzed all core files to understand the full flow: from Spring bean registration → MyBatis-Plus interceptor chain assembly → JSqlParser-based AST parsing and modification → WHERE clause injection logic
3. Explained the mechanism in Chinese, clarifying that PlusTenantLineHandler itself does not concatenate SQL but provides answers to three questions, while TenantLineInnerInterceptor + JSqlParser performs the actual AST rewriting
4. Provided concrete examples of SQL before/after injection and clarified the role of ignoreTable(), getTenantIdColumn(), and getTenantId()
5. Concluded with a one-sentence summary distinguishing the roles of the handler and the interceptor

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-tenant\src\main\java\org\dromara\common\tenant\core\PlusTenantLineHandler.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-mybatis\src\main\java\org\dromara\common\mybatis\config\MybatisPlusConfig.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-tenant\src\main\java\org\dromara\common\tenant\config\TenantAutoConfiguration.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-tenant\src\main\java\org\dromara\common\tenant\core\TenantContextHolder.java

## Notes
- None

## Task overview
Successfully completed the explanation task: provided a complete, accurate, and concise technical explanation of the PlusTenantLineHandler and TenantLineInnerInterceptor collaboration for SQL concatenation; clarified the role of JSqlParser; included concrete SQL examples; no errors or omissions.
