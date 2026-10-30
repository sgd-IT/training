---
title: "Nginx缺失代理配置导致iframe加载SPA"
usage_scenario:
    - "iframe嵌套页面显示异常或跳转到主页"
    - "访问特定后端路径却返回前端页面内容"
    - "排查Nginx静态资源与API代理冲突"
keywords:
    - "Nginx"
    - "proxy_pass"
    - "try_files"
    - "iframe"
    - "SPA"
source: "auto"
---

当后端接口路径（如/druid/*、/admin/*）在Nginx中缺少对应的proxy_pass配置时，由于Nginx通常配置了`try_files $uri $uri/ /index.html`的兜底规则，会导致该请求返回前端SPA的入口文件（index.html）。此时若该路径被嵌入到iframe中，iframe内将加载整个前端应用并触发路由跳转（如回到首页），而非预期的后端页面。

解决方案：
1. 检查Nginx配置文件，为缺失的路径添加正确的proxy_pass转发规则。
2. 或者在系统菜单配置中隐藏无用的监控入口。

来源工具：Grep, Read
