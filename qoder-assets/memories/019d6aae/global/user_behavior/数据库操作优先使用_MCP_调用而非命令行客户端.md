---
title: "数据库操作优先使用 MCP 调用而非命令行客户端"
usage_scenario:
    - "执行数据库查询或数据校验时"
    - "需要变更库表数据或执行DDL时"
    - "MCP权限受限需兜底执行时"
keywords:
    - "MySQL MCP 优先调用"
    - "数据库操作方式"
    - "mysql_query 工具"
    - "避免 CLI 试错"
source: "user"
---

用户要求：涉及数据库的查询、校验、变更，优先通过已启用的 MySQL MCP 服务执行（工具 mysql_query），不要反复用终端 mysql.exe CLI 试错。具体启用哪个 MCP 服务取决于当前工作区，服务名按项目库命名，例如 hc-standard 对应 hc-standard-test、anwei-inner 对应 anwei-inner-office，使用前先看已启用的服务列表确认。MCP 无法覆盖某个操作时（例如 DDL 被权限开关拒绝），要先明确说明被限制的原因，再请用户调整 MCP 配置或把兜底执行范围压缩到最小，兜底完成后仍回到 MCP 做校验。
