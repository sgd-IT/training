---
title: "消防生产库新增只读MySQL MCP实例并打通写入方法"
usage_scenario:
    - "为某套数据库新增只读权限的MCP实例"
    - "需要修改工作区外的JSON配置文件"
    - "验证新建MCP实例能否连通目标库"
keywords:
    - "只读MCP实例"
    - "anwei-fire-remote-monitor-prod"
    - "ALLOW写开关"
    - "工作区外文件写入"
    - "生产库冒烟测试"
source: "auto"
---

## Notes
- 【2026-10-17 复核】本条"两份 mcp.json 均处于启用状态"的结论已被推翻：本轮读取 c:\Users\Administrator\.qoder\mcp.json 发现 `anwei-fire-remote-monitor-prod` 条目实为 `"disabled": true`，CallMcpTool 直接报 `not found MCPHost severName: anwei-fire-remote-monitor-prod`。因此遗留事项"未在 Qoder 内重载并用 mysql_query 验证"应视为**未通过**：该实例当前不可用，需用户手工去掉 disabled 并重载 MCP 才能经 mysql_query 查生产库。
- 同一会话中可用的 MySQL MCP 实例仅 `anwei_fire_remote_monitor_test`（LAN 192.168.250.160）；生产库数据需按本条 Notes 既有方法改用本地 mysql.exe CLI 只读查询兜底。
