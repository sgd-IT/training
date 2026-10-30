---
title: "登录页默认落地页会被地址栏 redirect 参数静默覆盖"
usage_scenario:
    - "修改登录后默认跳转页面后不生效"
    - "排查登录后回到后台首页而非预期页面"
    - "设计路由守卫与登录页跳转协同"
keywords:
    - "redirect 查询参数"
    - "permission.ts 路由守卫"
    - "login.vue 跳转逻辑"
    - "默认落地页失效"
    - "constantRoutes 根路径重定向"
source: "auto"
---

Bug class：前端把"登录后默认落地页"改成某新页面后行为不生效，登录后仍跳回旧首页。

Root cause：路由守卫在拦截未登录访问时会把地址重定向为 `/login?redirect=<原目标>`（例如从根路径进入时被带上 `?redirect=/index`），而登录页原有逻辑是"优先使用 redirect 参数，仅在为空时才用默认值"，于是默认落地页配置被 URL 上残留的 redirect 完全覆盖。只改登录页里的默认值常量无法生效。

Fix pattern：若产品要求"登录后固定进某个页面"，必须在登录成功回调里无条件跳转到该固定路径，并删除登录页中读取 redirect 参数的响应式变量及其 watch（避免留下死代码）；若只想改默认值而不影响深链恢复，则应保留 redirect 但把根路径路由的 redirect 目标一并改掉。两种语义要分清后再动手。

Reusable lesson：不要只修改登录页里的默认跳转常量就认为跳转已生效，因为守卫注入的 redirect 参数优先级更高且只在真实登录流程中出现；必须确认"入口路径 → 守卫注入参数 → 登录页消费参数"这条链路，并用带 redirect 参数的完整流程验证。适用于 Vue Router + 路由守卫携带 redirect 参数的后台类项目；不适用于登录成功后不做任何 push 的单页嵌入或 SSO 回调场景。
