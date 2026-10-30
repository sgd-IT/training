---
title: "OAuth2登录必须携带Basic Auth请求头"
usage_scenario: []
keywords:
    - "OAuth2"
    - "Basic Auth"
    - "Authorization头"
    - "client信息"
---

OAuth2标准登录流程要求客户端在调用/form认证接口时，必须在请求头中携带Authorization: Basic {base64(clientId:clientSecret)}，否则后端会抛出UnapprovedClientAuthenticationException。（来源：MonkeyDefaultAuthenticationSuccessHandler）
