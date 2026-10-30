---
title: "WebSocket 404因生产环境AppKey与AccessKey配置互换"
usage_scenario:
    - "WebSocket连接报404但URL格式正确"
    - "多环境配置中同一字段在不同环境值不一致导致服务不可达"
    - "通过日志URL反查配置项时发现开发/生产值颠倒"
keywords:
    - "WebSocket 404"
    - "AppKey配置错位"
    - "多环境配置一致性"
    - "application-prod.yml"
---

WebSocket连接返回HTTP 404，根本原因是生产环境application-prod.yml中AppKey与AccessKey的值被互换：开发环境AppKey=xFnQKpCv8xSRjm7zqh6s（正确用于WebSocket路径），而生产环境误填为MyDfMHwsW4A2xWLV8tEk。修复方式是将deploy/config/monitor-api/application-prod.yml中socket.AppKey改为xFnQKpCv8xSRjm7zqh6s，AccessKey改为MyDfMHwsW4A2xWLV8tEk。（来源：Read/Grep）
