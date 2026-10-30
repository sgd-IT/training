---
title: "Nginx try_files导致后端接口被SPA index.html吞没"
usage_scenario:
    - "访问后端管理页面（如Druid、Admin）时显示为空白或首页"
    - "Nginx未配置特定路径导致请求被前端SPA接管"
    - "排查前后端分离架构下的路由匹配问题"
keywords:
    - "Nginx"
    - "try_files"
    - "SPA"
    - "index.html"
    - "Druid"
source: "auto"
---

## 现象
访问后端地址（如 `/druid/index.html`）时，Nginx返回了前端SPA的`index.html`（HTTP 200），导致页面显示错误或跳转。

## 原因
Nginx配置中缺少该路径的`location`块，请求落入`location /`的`try_files $uri $uri/ /index.html`兜底逻辑，将SPA入口文件作为响应返回。

## 解决方案
1. **停用菜单**：若该功能不可用或不必要，直接通过SQL停用对应菜单（如`UPDATE system_menu SET visible = 0 WHERE id IN (...)`）。
2. **补充代理**：若需保留，在Nginx配置中添加对应的`location`块并指向后端服务（如`proxy_pass http://server:48080;`）。注意检查后端安全配置（如Druid需设置账号密码或使用nginx auth_basic保护）。

## 注意事项
此类问题与WebSocket等长连接被误拦截机制相同，排查时可先检查Nginx是否有对应location，以及Network面板中首个文档响应的Content-Type是否为text/html。
