---
title: "OkHttpClient连接复用与超时配置"
usage_scenario:
    - "优化HTTP请求性能时配置OkHttpClient"
    - "排查网络延迟或连接耗尽问题时检查客户端复用策略"
    - "新模块引入HTTP调用时确定客户端初始化方式"
keywords:
    - "OkHttpClient"
    - "连接复用"
    - "超时配置"
    - "HTTP性能"
---

项目中HTTP客户端应复用OkHttpClient实例以减少TCP/TLS握手开销，通过类级别静态单例+内置ConnectionPool实现连接复用，并设置connectTimeout(5s)和readTimeout(10s)防止阻塞。
