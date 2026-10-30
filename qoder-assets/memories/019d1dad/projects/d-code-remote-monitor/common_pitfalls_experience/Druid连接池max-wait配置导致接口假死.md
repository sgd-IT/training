---
title: "Druid连接池max-wait配置导致接口假死"
usage_scenario:
    - "接口响应极慢且超过前端超时时间"
    - "排查数据库连接池耗尽导致的阻塞"
    - "优化高并发场景下的连接池参数"
keywords:
    - "Druid"
    - "max-wait"
    - "连接池"
    - "性能排查"
source: "auto"
---

Druid连接池配置 `max-wait` 过大（如600000ms）会导致在连接耗尽时请求静默等待长达10分钟，而非快速失败。排查接口耗时过长（如>30s）时，若SQL执行时间很短但总耗时很长，应检查Druid监控页的 `WaitThreadCount` 和 `ActiveCount`。建议将 `max-wait` 调整为合理值（如5000ms），并适当增加 `max-active` 和 `min-idle` 以应对突发流量。（来源：Bash/配置文件分析）
