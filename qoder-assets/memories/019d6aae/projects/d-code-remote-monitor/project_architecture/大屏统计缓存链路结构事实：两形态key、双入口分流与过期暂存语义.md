---
title: "大屏统计缓存链路结构事实：两形态key、双入口分流与过期暂存语义"
usage_scenario:
    - "改造或排查大屏统计缓存命中率与串户问题"
    - "新增定时任务按指定租户取数时选择上下文建立方式"
    - "评审活跃租户预热窗口与缓存TTL时序配置"
keywords:
    - "DASHBOARD_ACTIVE_TENANTS"
    - "DASHBOARD_STATISTICS"
    - "warmUpStatistics"
    - "resolveEffectiveTenantId"
    - "executeIgnoreWithResult"
    - "信号量名额重建"
source: "auto"
---

anwei-fire-remote-monitor 大屏统计缓存（FireDashboardServiceImpl / FireDashboardWarmUpTask）经源码核实的结构不变量：

一、两个 Redis key 形态必须区分，混用即读写不通：
- 活跃视角集合：单个 ZSet（RedisKeyConstants.DASHBOARD_ACTIVE_TENANTS），key 不拼后缀；成员是"视角后缀"（数字租户 id 或字符串 all），分值是最后访问的毫秒时间戳。
- 统计缓存：每租户一个 String（RedisKeyConstants.DASHBOARD_STATISTICS + 视角后缀），值为统计 JSON + buildAt。

二、生效租户解析只有一处口径：resolveEffectiveTenantId 先取登录租户，登录租户为 null 时直接 return null（前端传入的 tenantId 被丢弃，退化为全量视角）；非 null 时只有超管可用传入 tenantId 覆盖，普通企业用户传什么都无效。缓存 key 与查询上下文都只认这一个解析结果。

三、双入口分流（预热与请求侧共用 getOrRebuildStatistics，但建上下文方式不同）：
- tenantId == null 必须走 TenantUtils.executeIgnoreWithResult（只置 ignore=true、不改 tenantId）。原因是定时任务线程无租户上下文，若走常规路径 TenantDatabaseInterceptor.getTenantId() 会调 getRequiredTenantId() 抛"租户 ID 不能为空"。
- tenantId != null 由 getOrRebuildStatistics 内部 TenantUtils.execute(effectiveTenantId, ...) 建上下文（同时强制 ignore=false）。

四、getOrRebuildStatistics 的过期分支不直接返回旧值：命中且新鲜才立即返回；过期时把旧值暂存为 staleData 并继续抢信号量名额重建，旧值只在"没抢到名额"或"重建抛异常"两种情况下才真正返回，此时可能返回 null（前端拿到空统计而非报错）。

五、时序约束：预热 fixedDelay=60s 必须小于新鲜期 70s，否则预热产出的缓存在下次请求前已判过期；markActiveTenant 只允许在请求侧（loadStatisticsCached）调用，预热侧回写会让活跃窗口自我续命、永不过期。

六、调用方分布：dashboard/statistics 除 admin 与 app 两端 controller 外，还被 module-ai 的 AiToolService.executeGetDashboardOverview 以 TenantUtils.getCurrentTenantId() 作为入参调用——该值在无登录上下文的线程里为 null，会连带把 all 标成活跃。
