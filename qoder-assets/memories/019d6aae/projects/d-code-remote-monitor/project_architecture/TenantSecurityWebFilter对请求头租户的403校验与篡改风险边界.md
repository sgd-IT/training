---
title: "TenantSecurityWebFilter对请求头租户的403校验与篡改风险边界"
usage_scenario:
    - "评估服务层租户取值写法的安全等级"
    - "排查跨租户请求返回403的原因"
    - "设计指定租户查看功能的越权防护"
keywords:
    - "TenantSecurityWebFilter"
    - "tenant-id header校验"
    - "403越权拦截"
    - "TenantContextHolder风险边界"
    - "biz-tenant starter"
source: "auto"
---

framework/spring-boot-starter-biz-tenant 的 TenantSecurityWebFilter.doFilterInternal 对请求头租户做前置校验：已登录用户若上下文中无租户 id，则用其登录用户所属租户写入上下文；若请求头带的租户与该账号可访问范围不一致，直接返回 403「您无权访问该租户的数据」并中断请求。因此企业账号伪造 tenant-id header 在 HTTP 入口即被拦截，不会落到业务层。

推论（改造时必须知道的边界）：服务层用 TenantContextHolder.getTenantId() 判定身份，其风险主要来自代码内 TenantUtils.execute(target, ...) 对上下文的临时改写（块内读到的是"要查看的租户"而非登录租户），而不是外部 header 篡改——后者已由过滤器兜住。据此可区分两类问题的严重级：裸 supplier.get() 之类"没显式锁定登录租户"的写法属于健壮性/契约缺失（P1），不是可被外部触发的越权漏洞（P0）；而在 execute 块内误用上下文值做身份判定则是真实的逻辑错误。
