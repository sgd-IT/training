---
title: "零售店数据隔离双层放行：SQL层PlusTenantLineHandler与业务层RetailDataScopeHelper协同机制"
usage_scenario:
    - "新增跨店视野角色或接口"
    - "排查未绑定零售店报错"
    - "理解项目数据隔离机制"
keywords:
    - "retail_id"
    - "PlusTenantLineHandler"
    - "RetailDataScopeHelper"
    - "SQL层拦截器"
    - "业务层工具类"
    - "regulator角色放行"
source: "auto"
---

anwei-fireworks-retail 的零售店数据隔离是**双层结构，缺一不可**：
① SQL 层：PlusTenantLineHandler（ruoyi-common-tenant，由租户插件改造：租户列=retail_id），所有 fireworks_ 前缀表查询自动追加 retail_id=?；取不到值 fail-closed 抛「当前用户未绑定零售店，无法执行数据隔离查询」。getTenantId 优先取 TenantContextHolder（无登录态链路），回落 LoginHelper.getRetailId()。
② 业务层：RetailDataScopeHelper（fireworks 模块），getAccessibleRetailIds() 返回 null 表示不限制。
特殊身份（superadmin、regulator）必须**两层都放行**：业务层加分支 + PlusTenantLineHandler.ignoreTable() 放行；只在业务层加分支会被 SQL 层拦截（2026-09-17 实际踩坑）。非 fireworks_ 前缀系统表、fireworks_retail_info、fireworks_alarm_scene_config 表不隔离。

PlusTenantLineHandler.ignoreTable() method must explicitly check for 'regulator' role in LoginHelper.getLoginUser().getRolePermission() to bypass SQL-layer tenant interception for regulatory cockpit interfaces. This is required because business-layer RetailDataScopeHelper already supports regulator, but SQL-layer interceptor does not — causing 'current user not bound to retail store' errors unless both layers are configured.
