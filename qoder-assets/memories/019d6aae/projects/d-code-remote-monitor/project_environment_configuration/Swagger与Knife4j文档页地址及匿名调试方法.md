---
title: "Swagger与Knife4j文档页地址及匿名调试方法"
usage_scenario:
    - "需要在浏览器快速测试后端接口找文档地址"
    - "排查 swagger-config 或 api-docs 返回空列表或401"
    - "判断某接口能否免登录在文档页调试"
keywords:
    - "doc.html"
    - "swagger-config"
    - "Knife4j"
    - "v3/api-docs"
    - "接口调试"
    - "48080端口"
source: "auto"
---

anwei-fire-remote-monitor 后端（server.port=48080）自带接口文档页，可零工具在浏览器直接测接口（2026-09-30 实测验证）：
- Knife4j 中文界面：`http://<host>:48080/doc.html`；原生 Swagger UI：`http://<host>:48080/swagger-ui/index.html`。本机调试用 localhost，测试服务器用 192.168.250.144。
- 分组数据端点为无前缀路径 `/v3/api-docs/{group}` 与 `/v3/api-docs/swagger-config`，均**无需登录**即可访问；分组有 ai、all、eiot、infra、system（由 EnjoySwaggerAutoConfiguration 按 `/admin-api/{path}/**` 与 `/app-api/{path}/**` 匹配生成）。
- 坑：带前缀的 `/admin-api/v3/api-docs`、`/admin-api/v3/api-docs/swagger-config` 会被安全过滤器拦截返回业务 401（HTTP 状态仍是 200，body 是 401 JSON，易误判为可用）。
- `@PermitAll` 且已进 permit-all_urls 白名单的开放接口（如 `/app-api/open/camera-alert/report`）在文档页"调试"里可直接发送，不需要 token；普通 admin-api 接口调试则需先登录取 token。
- 项目登录接口 `/admin-api/system/auth/login` 需要验证码参数（缺 token 字段报"登录请求令牌不能为空"），命令行脚本难以直接匿名登录。
