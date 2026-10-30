---
title: "移动端登录三端点须加入 platform.tenant.ignore-urls"
usage_scenario:
    - "新增/app-api登录端点排查被租户拦截"
    - "编辑application.yaml租户忽略名单"
    - "区分租户忽略与安全permitAll配置"
keywords:
    - "tenant ignore-urls"
    - "app-api登录端点"
    - "mobile-login"
    - "application.yaml白名单"
    - "租户拦截器"
source: "auto"
---

后端仓库 anwei-fire-remote-monitor 的 server/src/main/resources/application.yaml 中，platform.tenant.ignore-urls 需登记移动端 uni-app 走 /app-api 前缀的三个登录相关端点：/app-api/system/auth/mobile-login、/app-api/system/auth/wx-ma-phone、/app-api/system/auth/generate-login-nonce。原因：这些接口在登录前没有 tenant-id，若不登记会被租户拦截器（TenantSecurityWebFilter/TenantDatabaseInterceptor）拦掉；admin-api 前缀的同名接口此前已登记，本次补齐 app-api 侧缺口。注意语义区分：这里配置的是"租户上下文跳过"（tenant ignore-urls），不是 Spring Security 的免登录名单——后者由 @PermitAll 注解在启动时自动收集，无需写进 yaml。提交此类改动时应把 profiles.active 临时改回 prod 隔离环境行，只暂存白名单新增行。
