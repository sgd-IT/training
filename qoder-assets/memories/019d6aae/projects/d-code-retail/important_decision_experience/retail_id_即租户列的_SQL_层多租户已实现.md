---
title: "retail_id 即租户列的 SQL 层多租户已实现"
usage_scenario:
    - "When evaluating whether to implement full multi-tenancy in a RuoYi-Plus based retail system"
keywords:
    - "retailId"
    - "multi-tenancy"
    - "data isolation"
    - "RuoYi-Plus"
source: "auto"
---

Conclusion: 已实现 SQL 层多租户，租户列直接复用 fireworks_* 表已有的 retail_id（VARCHAR(36)），租户值 = 登录用户 retailId（LoginHelper token extra），由 ruoyi-common-tenant 模块的 PlusTenantLineHandler + TenantLineInnerInterceptor 自动拼接，tenant.enable 开关控制。
Rationale (trade-off): 不新增 tenant_id 列——字段、索引、存量数据、登录态透传全部现成，SQL 迁移与登录链路改造整体取消；白名单只认 fireworks_ 前缀（排除 retail_info/alarm_scene_config 两张无 retail_id 的表），sys_* 天然跳过；超管跳过过滤与 RetailDataScopeHelper 语义对齐；业务层 retailId 校验保留作双保险。
Rejected alternatives: 新增 tenant_id 列 + sys_tenant 表 + 租户管理页（官方 RuoYi-Plus 全家桶）——改造面大且用户已明确拒绝；恢复上游 ruoyi-common-tenant——依赖体系不匹配且黑名单式表过滤易漏。
Applicable / expiry conditions: 单经营方多店场景成立；若未来出现跨店集团账号（一个用户管多个店的数据域），retail_id 单值租户不够用，需升级为 IN 列表过滤或真正的 tenant_id 维度。
