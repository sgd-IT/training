---
title: "TenantLineInnerInterceptor 已在 MybatisPlusConfig 中注册于 dataPermission 之前"
usage_scenario:
    - "验证多租户 SQL 拦截器是否生效"
    - "排查 retail_id WHERE 条件未自动拼接问题"
    - "调整 MyBatis-Plus 拦截器链顺序时"
keywords:
    - "TenantLineInnerInterceptor"
    - "MybatisPlusConfig"
    - "ObjectProvider TenantLineHandler"
    - "retail_id SQL注入"
    - "dataPermission前序"
source: "auto"
---

The TenantLineInnerInterceptor is now registered in MybatisPlusConfig.java via ObjectProvider<TenantLineHandler> and placed at the head of the interceptor chain (before dataPermission). This enables tenant line SQL injection (adding retail_id WHERE clauses automatically) at runtime.
