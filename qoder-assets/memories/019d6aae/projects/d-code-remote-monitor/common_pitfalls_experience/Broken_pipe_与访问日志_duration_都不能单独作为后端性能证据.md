---
title: "Broken pipe 与访问日志 duration 都不能单独作为后端性能证据"
usage_scenario:
    - "排查线上接口偶发500是否为真后端故障"
    - "依据接口访问日志判定线上性能瓶颈归属"
    - "评估慢请求耗时该归客户端还是服务端"
keywords:
    - "AsyncRequestNotUsableException"
    - "Broken pipe"
    - "duration非SQL耗时"
    - "user_ip横向对比"
    - "连接池max-wait串行等待"
    - "CDB SET GLOBAL需SUPER"
source: "auto"
---

Bug class: AsyncRequestNotUsableException / Broken pipe 出现在接口错误日志里，容易被误判为后端 bug 或慢 SQL；反向的错是拿访问日志 duration 反推 SQL 耗时导致误定责。

Root cause: 'ServletOutputStream failed to write: java.io.IOException: Broken pipe' 是客户端在响应写完成前断开连接所致（前端 axios timeout 30 秒先掐断、用户刷新/关闭页面）。堆栈里出现 AbstractMessageConverterMethodProcessor.writeWithMessageConverters 且无任何 Controller/Service/Mapper 帧，即证明业务已执行完、纯属写失败。GlobalExceptionHandler.defaultExceptionHandler 对 Exception.class 兜底 log.error + createExceptionLog + 再返回一次响应，于是记成假 500 并污染 infra_api_error_log。
但客户端断开只是"最后一步"，长耗时本身常有服务端并发贡献，两者可同时成立：本项目 dashboard/statistics 单次串行跑十几条聚合查询，每个子查询各等一次连接池（Hikari max-wait=600000），缓存未命中时惊群重算，十几条累加可达数千秒量级——实测 avg 261s / max 8034s 与该机制吻合。同时 duration 覆盖整个请求生命周期（业务处理 + 响应体写出网络全过程），客户端链路差或浏览器同域仅 6 个并发连接排队时这段阻塞全计入 duration，故既不能据 Broken pipe 把责任 100% 推给客户端，也不能拿 duration 当"某条 SQL 慢"的证据。

Fix pattern（定责顺序）: ①按 user_ip / UA 横向对比同一接口耗时分布（GROUP BY user_ip, result_code），失败若集中于单一出口 IP（实测 219.131.130.21）则为该处链路或多终端并发问题；②看 infra_api_error_log 的 exception_name 分类（SQLSyntaxErrorException/Unknown column 指向库结构，AsyncRequestNotUsableException+Broken pipe 指向客户端断开，403 立即返回指向权限）；③统计同 IP 各接口调用量识别轮询风暴；④核对服务端连接池 max-wait 与单请求内串行 DB 访问次数，判断慢是排队还是计算。要量化单条 SQL 只能取服务端数据源：MySQL 慢日志或 performance_schema.events_statements_summary_by_digest；本机 mysql.exe 直连云库的计时含公网 RTT，不可当线上 SQL 耗时。只有同接口在所有客户端一致缓慢且慢日志命中对应语句时，才动 SQL/索引。

Reusable lesson: Don't 见到 AsyncRequestNotUsableException 就断定后端有 bug，也 Don't 据 Broken pipe 跳过服务端连接池与串行查询核查；instead 先按来源 IP 横向对比、再核服务端容量项、最后用慢日志/APM 取证。适用于依赖自建接口访问日志（result_code + duration）做线上性能定责，以及 Spring MVC 写响应阶段异常被全局兜底记成 500 的排查。不适用于：堆栈指向业务逻辑帧的场景；接口在所有客户端一致缓慢且慢日志已命中语句的情况；WebSocket/流式接口（duration 语义不同）。边界补充：腾讯云 CDB 上 root 执行 SET GLOBAL long_query_time 会被拒（ERROR 1227 requires SUPER），performance_schema 摘要表可能为空、log_output=FILE 时 mysql.slow_log 恒 0 行，阈值只能走控制台参数设置。
