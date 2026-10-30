---
title: "sys-platment日志注释关闭配置"
usage_scenario:
    - "修改logback.xml时确定sys-platment日志的禁用方式"
    - "排查日志文件膨胀问题时参考已确认的禁用方案"
    - "新环境部署时复用该日志精简配置"
keywords:
    - "sys-platment"
    - "logback注释"
    - "日志关闭"
    - "日志精简"
---

项目日志配置中，sys-platment相关appender和logger需注释关闭，仅保留level="error"的logger定义，使其错误日志通过root通道写入sys-error.log，不再生成独立的sys-platment.log文件，也不再向sys-info.log重复输出。
