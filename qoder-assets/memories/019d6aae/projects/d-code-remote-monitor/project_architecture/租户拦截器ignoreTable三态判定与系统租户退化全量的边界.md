---
title: "租户拦截器ignoreTable三态判定与系统租户退化全量的边界"
usage_scenario:
    - "新建需全系统共享免租户隔离的表时"
    - "排查SQL被自动追加tenant_id条件报错"
    - "实现或排查跨租户指定查看功能"
keywords:
    - "TenantDatabaseInterceptor"
    - "ignoreTable判定链"
    - "ignore-tables配置清单"
    - "全系统共享表建表"
    - "系统租户退化全部视角"
    - "TenantUtils.execute作用域"
source: "auto"
---

framework/spring-boot-starter-biz-tenant 的 TenantDatabaseInterceptor.ignoreTable 决定是否给 SQL 追加 tenant_id 条件，其真实判定链（已逐行读源码核实）为三层：① TenantContextHolder.isIgnore() 为 true → 跳过过滤；② 表在 TenantProperties.getIgnoreTables() 配置内 → 跳过过滤；③ 登录用户带 INFO_KEY_IS_SYSTEM_TENANT=true（由 OAuth2TokenServiceImpl.fillTenantAndRoleInfo 按租户套餐是否为系统套餐写入令牌）时，比较"上下文租户 id"与"登录用户租户 id"：不相等说明被 TenantUtils.execute 切换到特定租户 → 返回 false 即按上下文租户过滤；相等说明未切换 → 返回 true 即不加任何 tenant_id 条件、看到全部租户数据。非系统租户账号进不了第③层，直接返回 false，永远按上下文租户过滤。

由此推出两个改造时必须知道的结论：一是"用哪个租户执行"不需要改任何 SQL，只需改 TenantUtils.execute 传入的上下文值；二是超管（登录租户即系统租户 1）若把 tenantId=1 作为切换目标传入，会因"上下文==登录租户"重新命中跳过分支而退化成全部视角，因此前端租户下拉必须过滤掉系统租户自身。TenantUtils.execute 的实现是保存旧上下文→setTenantId+setIgnore(false)→执行回调→finally 还原，故查询必须写在回调内部，空回调等于什么都没做。

推论到建表场景：任何需要"全系统共享、不做租户隔离"的新表（如公告/配置/更新日志类），必须显式把表名加进 application.yaml 的 yudao.tenant.ignore-tables 清单，否则拦截器会自动追加 tenant_id 条件导致 SQL 报错或数据互相不可见；反之若表参与隔离则 DO 必须带 tenant_id 字段。

## Notes
- 关于第③层"非系统租户账号进不了第③层、永远按上下文过滤"的后半句表述过于绝对：后续会话实测（FireSpaceServiceImpl.listFireSpace 显式 eq(tenant_id) + 拦截器双保险场景）确认，Service 层显式加 tenant_id 条件仍是必要的防御写法——因为当登录用户为系统租户且上下文未被切换时，拦截器不加任何 tenant_id 条件，此时若不显式过滤就会返回全量。正确口径应改为："非系统租户账号不会被第③层放行到全量视角，但也不能依赖拦截器替自己收窄——业务需要限定单租户时必须在查询上显式 eq(tenant_id)"。最新结论以本条 Notes 为准。
- 补充边界（定时任务/异步线程场景）：上述三层判定全部以"存在登录用户"为前提。@Scheduled / 异步线程内没有 SecurityContext，凡依赖 SecurityFrameworkUtils.getLoginUserId() 的解析（如 UserTenantResolver.resolveLoginUserTenantId）一律返回 null，进而使"按登录租户收窄"的逻辑退化为全量视角；这类线程要按指定租户取数必须显式 TenantUtils.execute(目标租户, ...) 并配 @TenantIgnore，不能复用请求侧的租户解析入口。
