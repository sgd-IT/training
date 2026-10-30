---
title: "Vite代理ECONNREFUSED故障诊断与修复"
usage_scenario:
    - "前端开发时出现/vite http proxy error且报ECONNREFUSED"
    - "排查前后端端口配置不一致导致的代理失败"
    - "验证后端服务是否启动及端口监听状态"
keywords:
    - "Vite代理"
    - "ECONNREFUSED"
    - "端口配置"
---

## 任务描述
定位并解决前端 Vite 开发服务器代理请求连接被拒绝（ECONNREFUSED）问题。

## 执行过程
```mermaid
graph TD
    A[现象:/auth/code和/auth/tenant/list代理失败] --> B[读取vite.config.ts和.env.development]
    B --> C[确认代理目标为http://localhost:8088]
    C --> D[通过Grep搜索application.yml确认server.port: 8088]
    D --> E[结论：后端服务未启动]
    E --> F[提供IDEA运行配置和Maven启动命令]
```

## 任务总结
成功诊断出ECONNREFUSED根本原因为后端Spring Boot服务未启动，通过检查前端代理配置与后端application.yml端口一致性，确认端口匹配（8088），并给出两种启动方式（IDEA run config / Maven命令），确保前后端联调环境就绪。
