---
title: "Maven多环境Profile配置"
usage_scenario:
    - "了解项目如何区分开发、测试、生产环境的配置激活方式及关键环境变量"
keywords:
    - "Maven Profile"
    - "local/dev/prod"
    - "profiles.active"
    - "日志级别"
---

多环境配置通过Maven Profile管理：
- `local`：激活配置`profiles.active=local`，日志级别`info`
- `dev`：默认激活环境，`profiles.active=dev`，日志级别`info`
- `prod`：`profiles.active=prod`，日志级别`warn`
- 公共变量：`monitor.username=ruoyi`、`monitor.password=123456`
