---
title: "mcp.json服务禁用配置方式"
usage_scenario:
    - "需要临时停用某个MCP数据库服务时"
    - "多环境切换中关闭非目标数据库连接"
    - "排查服务冲突问题时快速禁用特定MCP实例"
keywords:
    - "mcp.json"
    - "disabled"
    - "服务禁用"
---

在mcp.json中通过添加`"disabled": true`字段可禁用指定MCP服务，例如禁用`mcp_server_mysql`时，在其配置对象内第一行加入该字段即可。
