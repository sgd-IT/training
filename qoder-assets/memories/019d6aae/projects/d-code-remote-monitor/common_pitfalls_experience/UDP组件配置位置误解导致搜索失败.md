---
title: "UDP组件配置位置误解导致搜索失败"
usage_scenario:
    - "当在UDP/TCP协议组件配置查找中遇到搜索失败时"
keywords:
    - "UDP组件"
    - "配置位置"
    - "数据库配置"
    - "搜索失败"
source: "auto"
---

Bug class: UDP组件配置位置错误假设
Root cause: 初始假设UDP组件配置在文件中，但实际上存储在数据库eiot_component表的config字段中；代码分析显示配置通过JsonUtils.parseObject(cfg, UdpConfig.class)从数据库加载
Fix pattern: 当文件搜索失败时，检查代码中的配置加载逻辑，确认配置来源是文件还是数据库
Reusable lesson: 不要在文件系统中盲目搜索UDP组件配置，因为实际配置存储在数据库中；应该先分析配置加载代码以确定真实来源。适用于UDP/TCP协议组件配置查找；不适用于纯文件配置的组件。
