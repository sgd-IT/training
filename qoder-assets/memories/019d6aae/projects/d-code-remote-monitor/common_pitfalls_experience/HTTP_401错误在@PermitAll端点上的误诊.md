---
title: "HTTP 401错误在@PermitAll端点上的误诊"
usage_scenario:
    - "调试 @PermitAll 开放回调接口返回 401 错误时"
    - "排查前端调用后端接口失败但服务端日志无异常时"
keywords:
    - "401 错误"
    - "@PermitAll"
    - "/app-api 前缀"
    - "addPathPrefix"
source: "auto"
---

Bug class: HTTP 401错误误诊
Root cause: 在调用 @PermitAll 的开放回调接口时，将返回的 401 '账号未登录' 错误错误地归因为认证失败，而实际根因是请求 URL 缺少框架自动挂载的 /app-api 前缀（由 EnjoyWebAutoConfiguration.addPathPrefix 配置），导致请求路径未命中 Spring Security 的 permit-all 白名单。
Fix pattern: 当调试 @PermitAll 接口的 401 错误时，应优先检查请求路径是否与框架的 addPathPrefix 规则匹配，并确认该路径是否已加入安全白名单（application.yaml 中的 permitAllUrls 列表），而非直接假设为认证问题。
Reusable lesson: 不要将 @PermitAll 接口的 401 错误默认等同于 '账号未登录'，因为这会导致在错误的方向上浪费大量调试时间；应该首先检查请求路径是否完整且符合框架的前缀挂载规则。适用于调试 @PermitAll 接口的 401 错误；不适用于需要认证的接口或其它状态码的错误。
