---
title: "RestTemplate连接复用技术选型"
usage_scenario:
    - "优化HTTP客户端性能时选择连接复用方案"
    - "排查网络延迟问题时确认HTTP连接复用能力"
keywords:
    - "RestTemplate"
    - "Keep-Alive"
    - "HttpURLConnection"
---

项目使用Spring管理的RestTemplate Bean，底层采用SimpleClientHttpRequestFactory和HttpURLConnection，原生支持HTTP Keep-Alive连接复用
