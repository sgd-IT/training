---
title: "RuoYi框架Token传递规范"
usage_scenario:
    - "APP或前端调用后端接口时设置Authorization头"
    - "调试接口返回401时排查Header格式"
    - "为移动端新增免菜单权限的后端接口时确认认证方式"
keywords:
    - "RuoYi"
    - "Authorization Header"
    - "Bearer token"
    - "认证格式"
source: "auto"
---

RuoYi框架要求认证token必须通过HTTP Header传递，Header名称为`Authorization`，值格式为`Bearer {token}`，其中`Bearer`与token之间必须有一个空格。
