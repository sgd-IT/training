---
title: "mcp.json 添加 anwei-mon-fireworks-retail 数据库配置并授权 CRUD 权限"
usage_scenario:
    - "当需要为 MCP 服务添加新的 MySQL 数据库配置时"
    - "当需要为数据库连接配置授予特定 CRUD 权限时"
    - "当 mcp.json 配置需要与后端 application.yml 保持同步时"
keywords:
    - "mcp.json"
    - "database configuration"
    - "MySQL"
    - "CRUD permissions"
    - "anwei-mon-fireworks-retail"
source: "auto"
---

## Task description
- Core requirement: 将当前数据库配置添加到 mcp.json 文件中，并为该数据库授予完整的 CRUD（CREATE/READ/UPDATE/DELETE）权限
- Task background: 项目使用 MySQL 数据库，配置信息分散在多个 application-dev.yml 和 application-prod.yml 文件中；mcp.json 是 MCP 服务的配置中心，需要将数据库连接参数以标准格式注入并启用所有操作权限

## Execution process
1. 从 ruoyi-admin 的 application-dev.yml 中提取数据库配置：Host=192.168.250.160, Port=3306, DB=anwei-mon-fireworks-retail, User=root, Password=[REDACTED]
2. 使用 PowerShell 脚本向 mcp.json 的 mcpServers 对象中添加名为 anwei-mon-fireworks-retail 的新条目，包含 npx 命令、MySQL 环境变量及 ALLOW_DELETE/INSERT/UPDATE_OPERATION=true 的权限设置
3. 修复 PowerShell JSON 格式化问题：移除 BOM 并使用 Node.js 重新格式化为标准的 2-space 缩进
4. 验证最终文件内容，确认新条目已正确写入且格式规范

## Related files
- c:\Users\Administrator\.qoder\mcp.json
- d:\code\retail\update_mcp.ps1
- d:\code\retail\anwei-fireworks-retail\ruoyi-admin\src\main\resources\application-dev.yml

## Notes
- MCP DDL 操作被拒绝，因 mcp.json 缺少 SCHEMA_DDL_PERMISSIONS 配置且 MCP 服务器未重启；需手动在 mcp.json 中添加 "SCHEMA_DDL_PERMISSIONS": "true" 并完全重启 Qoder 才能生效。

## Task overview
Successfully added the anwei-mon-fireworks-retail database configuration to mcp.json with full CRUD permissions. The configuration was correctly extracted from application-dev.yml, injected via a robust PowerShell+Node.js script pipeline, and formatted to match the existing JSON style. The final file is ready for MCP service consumption.

## Notes（修正）
- MCP DDL 操作被拒绝的真实根因是 mcp.json 缺少 ALLOW_DDL_OPERATION 环境变量（值为 "true"），而非 SCHEMA_DDL_PERMISSIONS；后者仅对带 schema 前缀的 SQL 生效，且其预期格式为 JSON 映射（如 {"anwei-mon-fireworks-retail": true}），直接赋值 "true" 无效；修改后必须完全重启 Qoder 进程才能生效。
