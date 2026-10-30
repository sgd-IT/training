---
title: "fireworks零售系统基于retail_id的SQL数据隔离实现"
usage_scenario:
    - "后续新增 fireworks_ 业务表时的租户隔离评估"
    - "排查数据可见性/SQL 拼接异常时定位租户链路"
    - "扩展无登录态链路或定时任务时的 wrap 规范"
keywords:
    - "retail_id"
    - "SQL 数据隔离"
    - "TenantLineInnerInterceptor"
    - "fireworks 表白名单"
    - "LoginHelper.isSuperAdmin"
source: "auto"
---

- 多租户实现：ruoyi-common-tenant 模块，租户列 = fireworks_* 表已有 retail_id（VARCHAR(36)），租户值 = 登录用户 retailId（LoginHelper token extra）
- 表过滤为白名单式：仅 fireworks_ 前缀且排除 fireworks_retail_info 与 fireworks_alarm_scene_config（无 retail_id 列）
- 开关 tenant.enable（默认 false）控制 TenantLineHandler 注册；MybatisPlusConfig 用 ObjectProvider 松耦合组装，TenantLineInnerInterceptor 位于拦截器链头（dataPermission 之前）
- 超管（LoginHelper.isSuperAdmin）跳过租户过滤；无租户值时 fail-closed 抛 ServiceException
- TenantUtils.execute/executeIgnore 用于无登录态链路（定时任务、设备上报），Callable 版业务异常原样抛出不包 RuntimeException
- 业务层 RetailDataScopeHelper 校验保留，与 SQL 层构成双保险，不删
