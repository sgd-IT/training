---
title: "RuoYi框架API认证Header格式要求"
usage_scenario:
    - "调用RuoYi后端接口返回401 Unauthorized"
    - "Postman或curl中设置token时Header名填错为token而非Authorization"
    - "RuoYi项目前端或脚本请求后端API时认证失败"
keywords:
    - "RuoYi"
    - "Authorization"
    - "Bearer token"
    - "401认证失败"
source: "auto"
---

RuoYi框架API认证必须使用 `Authorization` 请求头，值格式为 `Bearer <token>`（`Bearer`后必须有一个空格，再接token字符串）；若误用`token`等其他Header名，将返回401认证失败。（来源：RuoYi框架HTTP API调用）
