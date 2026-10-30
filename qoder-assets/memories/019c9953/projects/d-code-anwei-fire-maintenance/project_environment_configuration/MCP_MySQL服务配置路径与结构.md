---
title: "MCP MySQL服务配置路径与结构"
usage_scenario:
    - "手动修改MCP MySQL连接配置时定位文件路径"
    - "排查MCP连接异常时检查env字段完整性"
    - "新环境部署时初始化MCP配置"
keywords:
    - "mcp.json"
    - "MCP配置路径"
    - "env字段"
    - "MySQL连接参数"
---

MCP配置文件路径为`c:\Users\Administrator\.qoder\mcp.json`，其中`mcp_server_mysql`服务的数据库连接参数需配置在`env`对象内，包含`MYSQL_HOST`、`MYSQL_PORT`、`MYSQL_USER`、`MYSQL_PASS`、`MYSQL_DB`五个字段
