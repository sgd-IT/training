---
title: "小程序双环境运行时配置：NODE_ENV驱动的API地址切换"
usage_scenario:
    - "When configuring or debugging the mini-program's network connectivity in different environments"
    - "When building the mini-program for WeChat DevTools preview vs online release"
    - "When onboarding new developers to understand how environment-specific endpoints are resolved"
keywords:
    - "mini-program"
    - "dual environment"
    - "NODE_ENV"
    - "API address"
    - "environment switch"
source: "auto"
---

The mini-program uses a dual-environment configuration: development environment connects to HTTP local IP (e.g., http://192.168.250.130:48080/admin-api), while production environment connects to HTTPS domain (https://fire-remote-admin.anxunwang.cn:18080/admin-api); the switch is controlled by the NODE_ENV environment variable at build time — 'development' triggers the dev URL, 'production' triggers the prod URL.
