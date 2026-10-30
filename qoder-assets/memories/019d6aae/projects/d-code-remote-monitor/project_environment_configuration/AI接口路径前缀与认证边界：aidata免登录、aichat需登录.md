---
title: "AI接口路径前缀与认证边界：/ai/data免登录、/ai/chat需登录"
usage_scenario:
    - "构造AI接口请求头与URL前缀时"
    - "排查AI接口401或返回空数据归因"
    - "评估新增AI接口是否需要登录态"
keywords:
    - "admin-api前缀注入"
    - "ai/data白名单免认证"
    - "ai/chat需登录token"
    - "tenant-id适用边界"
    - "AiWebConfiguration"
source: "auto"
---

调用本仓库 AI 模块接口的路径与认证口径（按实际配置区分两类）：

- 路径前缀：所有 admin 端 AI 接口统一挂在 `/admin-api` 下，例如 `http://<host>:<port>/admin-api/ai/data/alarm/statistics`、`/admin-api/ai/chat/completions`。Controller 上的 `@RequestMapping("/ai/...")` 不含前缀，前缀由框架按 controller.admin 包自动注入。
- `/ai/data/**`：在测试环境的 `application.yaml` permit-all 白名单里，无需 token 即可直调；因跳过了登录态，租户上下文只能靠请求头 `tenant-id` 提供，缺失会返回空数据或 401。
- `/ai/chat/**` 及其余 AI 接口：**不在白名单内，必须携带登录 token**；此时租户上下文由登录态解析，不需要手工传 `tenant-id`。前端走 request 封装时该头由拦截器自动附加。

结论：判断"要不要带 tenant-id / 要不要 token"先看目标路径落在哪一类，不要笼统认为全部 AI 接口都免认证或都需要手工租户头。
