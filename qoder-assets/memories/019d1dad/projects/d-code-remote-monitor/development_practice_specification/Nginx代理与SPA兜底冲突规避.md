---
title: "Nginx代理与SPA兜底冲突规避"
usage_scenario:
    - "新增第三方工具页面（如Druid、Admin）的前端入口时"
    - "排查前端页面打开后显示内容异常（如大屏代替了监控页）时"
keywords:
    - "Nginx代理"
    - "SPA兜底"
    - "路由拦截"
source: "auto"
---

在Nginx中新增代理路径时，必须确保后端服务已正确配置并转发，防止SPA的try_files兜底逻辑将请求错误地重定向到首页或其他页面
