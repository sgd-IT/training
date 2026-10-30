---
title: "数据库取数前须以@@hostname确认MCP落点而非假定权威源"
usage_scenario:
    - "多库环境下确定取数通道与结论归属"
    - "MCP与脚本查询结果不一致时判定可信源"
    - "汇报数据规模前声明所连实例"
keywords:
    - "@@hostname验证"
    - "anwei1-0001正式库"
    - "192.168.250.160测试库"
    - "跨环境结论归属"
source: "auto"
---

数据库环境验证的权威源选择：不存在固定可信连接，必须先 SELECT @@hostname 确认落点再取数。
- 当前通道事实：可用 MCP 服务名为 tgr-gz-vue（工具 mysql_query，READ-ONLY），落点 anwei1-0001:3306（正式库 220.203.23.144）；hc-standard-test 连测试库 192.168.250.160。两者是不同实例，结论不可互相推断。
- 历史误判教训：曾由直连 192.168.250.160 开发库的脚本得出票数 79，而正式库为 136，因未确认落点导致误判。
- 沿用口径：需要正式库结论时必须走能落到 anwei1-0001 的通道（tgr-gz-vue MCP 只读，或命令行 mysql.exe 最小范围只读 SELECT），并显式声明所连实例；写库/DDL 需另行确认有写权限的通道。调用报 "not found MCPHost severName" 时先复核是否误用了缓存目录中已 disabled 的服务名，而不是直接断定 MCP 失效。
