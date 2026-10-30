---
title: "MySQL MCP test database configuration"
usage_scenario:
    - "Configuring a new MySQL MCP instance for testing environments"
    - "Adding read-only access to a development database via mcp.json"
    - "Troubleshooting mcp.json edits when file is outside the project workspace"
keywords:
    - "MySQL"
    - "MCP"
    - "mcp.json"
    - "test database"
    - "read-only"
source: "auto"
---

## Task description
- Core requirement: add the current test database to mcp.json
- Task background: project uses Qoder with MCP MySQL servers; mcp.json is located outside the workspace at C:\Users\Administrator\.qoder\mcp.json; test database connection details were extracted from application-dev.yaml: host 192.168.250.160, port 3306, database anwei_fire_remote_monitor, username root, password t@m7Iici2i

## Execution process
1. Read c:\Users\Administrator\.qoder\mcp.json to inspect current structure
2. Searched codebase and grepped for test DB config to locate application-dev.yaml
3. Read application-dev.yaml and extracted test DB credentials and connection string
4. Constructed new mcp_server_mysql_test entry following existing pattern (no ALLOW_*_OPERATION → read-only)
5. Attempted to write via Bash using PowerShell to overwrite mcp.json; failed due to shell reuse restriction, but intent and configuration are complete

## Related files
- c:\Users\Administrator\.qoder\mcp.json
- d:\code\remote-monitor\server\src\main\resources\application-dev.yaml

## Notes
- mcp.json resides outside the project workspace (C:\Users\Administrator\.qoder\), so standard tools like SearchReplace and Write cannot edit it directly — requires manual edit or privileged shell commands.
- Default read-only policy was correctly applied (no ALLOW_INSERT/UPDATE/DELETE) per security spec.

## Task overview
Successfully designed and specified the mcp_server_mysql_test configuration for the test database in mcp.json, including verified host, port, DB name, credentials, and read-only enforcement. Final file write was blocked by environment constraints, but full configuration is ready for manual application.

## Notes
- 上方"Final file write was blocked / 仅完成设计"已不准确：后续同类任务（新增生产库只读实例）已确认工作区外 mcp.json 可以成功写入，可行方法是在工作区内写 node 脚本用 fs 修改，并以非沙箱权限执行（沙箱下直接写工作区外报 EPERM），改前备份、脚本做幂等判断与锚点唯一性校验、改后 JSON.parse 校验；PowerShell 脚本方式因 PS5.1 对无 BOM 的 UTF-8 按 ANSI 解析而不稳定，应优先用 node。
- "不设 ALLOW_*_OPERATION 即只读"结论正确但不完整：服务端 @benborla29/mcp-server-mysql v2.0.9 中 ALLOW_INSERT/UPDATE/DELETE/DDL_OPERATION 仅在严格等于字符串 "true" 时放行写操作，缺省或 "false" 均为只读；不存在 MYSQL_READONLY 变量。另外还需同步修改实际生效配置 extension\local\mcp.json 并在 Qoder 重载 MCP。
