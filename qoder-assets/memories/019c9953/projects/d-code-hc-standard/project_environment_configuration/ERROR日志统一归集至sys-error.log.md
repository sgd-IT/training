---
title: "ERROR日志统一归集至sys-error.log"
usage_scenario:
    - "配置新模块logger时避免重复创建error日志文件"
    - "审查logback.xml是否符合统一错误日志归集规范"
    - "排查日志分散问题时确认各模块错误日志归属"
keywords:
    - "ERROR日志归集"
    - "sys-error.log"
    - "logback统一配置"
---

项目所有 ERROR 级别日志统一由 root logger 的 `file_error` appender 写入 `sys-error.log`，不再为特定模块（如 `sys-platment`）单独维护错误日志文件。
