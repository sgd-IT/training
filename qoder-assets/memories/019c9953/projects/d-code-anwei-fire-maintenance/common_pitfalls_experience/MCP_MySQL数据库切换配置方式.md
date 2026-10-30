---
title: "MCP MySQL数据库切换配置方式"
usage_scenario:
    - "MCP服务连接了错误的MySQL数据库"
    - "通过修改mcp.json切换MCP服务的MySQL连接目标"
    - "排查MCP服务实际连接的数据库与预期不符"
keywords:
    - "MCP"
    - "MySQL"
    - "mcp.json"
    - "MYSQL_DB"
    - "数据库切换"
---

MCP MySQL服务数据库切换需修改`c:\Users\Administrator\.qoder\mcp.json`中对应服务（如`mcp_server_mysql1`）配置项下的`env.MYSQL_DB`字段，并重启该MCP服务才能生效。其他env字段（如MYSQL_HOST、MYSQL_PORT等）无需改动。（来源：CallMcpTool）
