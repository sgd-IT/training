---
title: "同步状态字段标准化与MCP数据库配置更新"
usage_scenario:
    - "当多个业务表需要统一添加同步状态字段时"
    - "当项目数据库连接从公网切换到内网时更新MCP配置"
keywords:
    - "sync_status"
    - "MCP配置"
    - "数据库迁移"
    - "ALTER TABLE"
source: "auto"
---

## Task description
- Core requirement: 为多个业务表添加标准的sync_status字段，并更新MCP数据库配置以匹配当前项目环境
- Task background: 项目中多个表（如base_work_special、base_contractor）缺少sync_status字段，导致SQL语法错误；同时，MCP配置指向公网地址，而项目实际使用内网数据库，需要更新MCP配置

## Execution process
1. 为base_work_special表执行ALTER TABLE语句，添加sync_status和sync_result字段，符合项目标准定义
2. 为base_contractor表执行相同ALTER TABLE语句，解决新的SQLSyntaxErrorException
3. 修改mcp.json文件，在mcpServers中新增hc-standard条目，指向内网数据库地址192.168.250.160:3306/tgr-gz-vue
4. 重启Qoder应用，使新的MCP服务器生效
5. 为base_work_video、base_hidden_trouble、platement_implement_task表执行ALTER TABLE语句，添加sync_status和sync_result字段
6. 创建base_work_certificate表，用于存储作业票操作证数据

## Related files
- c:\Users\Administrator\.qoder\mcp.json

## Notes
- None

## Task overview
成功完成同步状态字段标准化与MCP数据库配置更新：为base_work_special、base_contractor、base_work_video、base_hidden_trouble、platement_implement_task表添加了sync_status字段；在mcp.json中新增了hc-standard MCP服务器配置，指向项目当前使用的内网数据库；创建了base_work_certificate表；重启Qoder后新配置生效。
