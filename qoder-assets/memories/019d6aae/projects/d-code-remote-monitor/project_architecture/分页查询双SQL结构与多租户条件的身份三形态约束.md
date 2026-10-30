---
title: "分页查询双SQL结构与多租户条件的身份三形态约束"
usage_scenario:
    - "排查分页接口慢查询并还原真实SQL"
    - "评估COUNT统计开销或改造游标翻页"
    - "按登录身份分析租户过滤条件差异"
keywords:
    - "PaginationInnerInterceptor双SQL"
    - "COUNT全量统计"
    - "tenant_id三形态"
    - "ignore-tables白名单"
    - "ROWS_EXAMINED取证"
    - "Mapper debug日志"
source: "auto"
---

后端分页查询（MyBatis-Plus）的真实 SQL 结构事实，核验于 EnjoyMybatisAutoConfiguration / EnjoyTenantAutoConfiguration / EnjoyDataPermissionAutoConfiguration / TenantDatabaseInterceptor / TenantAreaTenantIdDataPermissionRule / application.yaml：

- 走 selectPage 的分页接口每次翻页发出两条 SQL：列表 SQL 与 COUNT(*) SQL（PaginationInnerInterceptor 的 searchCount 默认 true）。COUNT 无 LIMIT，慢查询排查时必须先分离两者耗时，不能只 EXPLAIN 列表那条；"命中索引仍慢"的高频成因就是 count 侧非覆盖索引导致的大规模回表。
- 多租户插件对未在 ignore-tables 中的表自动追加 tenant_id 条件，且形态取决于登录身份：企业租户用户拼 tenant_id=<当前租户>；系统租户且非监管角色（多数超管账号）完全不拼 tenant_id 条件（全租户可见）；系统租户且监管角色由 TenantAreaTenantIdDataPermissionRule 拼 tenant_id IN(区域内租户列表)，无区域配置时为 tenant_id = -1。同一接口用不同账号测的是不同的 SQL。
- platform.tenant.ignore-tables 白名单仅含 system_tenant、system_tenant_package 两张表，业务表（含 infra_api_access_log）全部受租户过滤。
- MyBatis 插件注册顺序为 DataPermissionInterceptor、TenantLineInnerInterceptor 经 addInterceptor 前置，PaginationInnerInterceptor 在拦截器构造时已加入。
- 抓取真实 SQL 的零代码改动方法：将对应 Mapper 接口的 logging.level 设为 debug，控制台输出 ==> Preparing / ==> Parameters；MySQL 侧可查 performance_schema.events_statements_history_long 的 SQL_TEXT / ROWS_EXAMINED / TIMER_WAIT。
