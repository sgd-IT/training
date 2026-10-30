---
title: "MySQL MCP server registration fails with 'no tools registered'; restart Qoder to load"
usage_scenario:
    - "调用MySQL MCP工具时报no tools registered for server错误"
    - "新增MCP服务器后首次调用失败"
    - "Qoder中MCP服务不可用时的首要排查动作"
    - "npx运行MCP Server时报ENOTEMPTY或EPERM错误"
    - "MCP Server启动失败且日志显示npm cache rmdir失败"
    - "context deadline exceeded错误伴随npm error ENOTEMPTY"
keywords:
    - "MCP server registration"
    - "Qoder restart"
    - "no tools registered"
    - "MCP service not loaded"
source: "auto"
---

MySQL MCP服务器（mcp_server_mysql）注册失败报错 `no tools registered for server`，根本原因是MCP服务未加载，必须重启Qoder应用才能使新注册的MCP服务器生效。（来源：CallMcpTool）

npx执行MCP Server时因缓存目录损坏（如C:\Users\Administrator\AppData\Local\npm-cache\_npx\57e5b9c57ed85305中文件被node进程锁定）报ENOTEMPTY或EPERM错误，导致MCP初始化失败并触发'context deadline exceeded'超时。解决方案：使用--cache参数指定全新缓存路径，例如：npx --cache "C:\Users\Administrator\AppData\Local\npm-cache-new" -y @benborla29/mcp-server-mysql（来源：Bash）

此外，在任务中确认了base_work_video、base_hidden_trouble、platement_implement_task三个表的sync_status字段定义，以及base_work_certificate表的创建。
