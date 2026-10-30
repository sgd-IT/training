---
title: "mcp_server_mysql2数据库操作权限配置"
usage_scenario:
    - "为mcp_server_mysql2配置数据库写权限时设置对应环境变量"
    - "排查数据库写操作失败原因时检查这些开关"
    - "重启服务前确认权限配置已正确加载"
keywords:
    - "mcp_server_mysql2"
    - "环境变量"
    - "CRUD权限"
    - "服务重启"
---

mcp_server_mysql2服务通过环境变量控制数据库操作权限：
- ALLOW_INSERT_OPERATION="true" 启用插入权限
- ALLOW_UPDATE_OPERATION="true" 启用更新权限
- ALLOW_DELETE_OPERATION="true" 启用删除权限
修改后需重启mcp_server_mysql2服务生效。
