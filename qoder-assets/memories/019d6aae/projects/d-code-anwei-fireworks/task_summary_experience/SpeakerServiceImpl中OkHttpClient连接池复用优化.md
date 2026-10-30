---
title: "SpeakerServiceImpl中OkHttpClient连接池复用优化"
usage_scenario:
    - "修复HTTP客户端重复实例化导致连接无法复用的问题"
    - "同类服务中优化OkHttp连接复用以降低网络延迟"
    - "排查高频率HTTP请求握手开销过大时的典型修复路径"
keywords:
    - "OkHttpClient"
    - "连接池复用"
    - "SpeakerServiceImpl"
---

## 任务描述
修复 SpeakerServiceImpl 中三处重复创建 OkHttpClient 导致连接无法复用的问题，通过改为类级别静态单例并显式配置 ConnectionPool，减少 TCP/TLS 握手开销。

## 执行过程
```mermaid
graph TD
    A[需求:减少HTTP握手] --> B[定位重复new OkHttpClient位置]
    B --> C[确认三处方法内局部实例化]
    C --> D[分析本质：每个new独立ConnectionPool，连接不共享]
    D --> E[改为static final OkHttpClient类字段]
    E --> F[显式配置ConnectionPool(5, 5min)并添加超时]
    F --> G[删除三处方法内new语句]
```

## 任务总结
成功将 OkHttpClient 改为类级别静态单例，显式配置 ConnectionPool 并设置 connect/read 超时；三处方法不再各自创建 client，复用同一连接池，同一 IP 请求可复用 TCP 连接，避免重复握手。
