---
title: "MCP MySQL服务数据库切换需修改mcp.json env.MYSQL_DB并重启"
usage_scenario:
    - "MCP MySQL服务连接数据库未按预期切换"
    - "修改mcp.json中MYSQL_DB后查询仍返回旧数据库名"
    - "排查MCP服务数据库连接配置失效问题"
keywords:
    - "MCP"
    - "mcp.json"
    - "MYSQL_DB"
    - "服务重启"
---

MCP服务`mcp_server_mysql1`连接的数据库由`c:\Users\Administrator\.qoder\mcp.json`中该server配置项下的`env.MYSQL_DB`字段决定，修改后必须重启MCP服务才能生效。（来源：CallMcpTool）
