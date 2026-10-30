---
title: "MySQL MCP 现状：tgr-gz-vue 只读连接，落点 anwei1-0001"
usage_scenario:
    - "执行数据库查询前确认MCP落点与可见库"
    - "CallMcpTool报not found时换正确服务名重试"
    - "MCP只读或闪断时选择兜底通道"
keywords:
    - "tgr-gz-vue"
    - "anwei1-0001"
    - "只读MCP"
    - "缓存目录服务名误导"
    - "mysql.exe兜底"
source: "user"
---

当前 MySQL MCP 连接现状（2026-10-15 复测）：
1. 可用服务名是 tgr-gz-vue（工具 mysql_query，READ-ONLY 只读）；默认库 tgr-gz-vue，落点 anwei1-0001:3306（正式库，全局 mcp.json 中 MYSQL_HOST=220.203.23.144 证实）；可见库含 tgr-gz-vue 与 tgr-gz-vue_copy。
2. 易踩坑：项目 mcps 缓存目录仍会列出 anwei_fire_remote_monitor_test（带 mysql_query 工具定义），但该条目在全局配置中已 disabled，用这个名字调用报 "not found MCPHost severName"；不要被缓存目录误导，直接用 tgr-gz-vue。
3. 该 MCP 可能偶发闪断：同一会话先正常返回、随后报 not found MCPHost 时先重试，仍不行再走兜底通道。
4. 测试库（192.168.250.160）与正式库（anwei1-0001）数据不同，任何结论前先 SELECT @@hostname 确认落点；当前 MCP 为只读，无 DDL/写权限，写库需走兜底通道。
5. 兜底通道（实测可用）：D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe 直连做最小范围只读 SELECT；正式库地址与凭据一律从 ruoyi-admin/src/main/resources/application-druid.yml 现读，不在记忆里留明文；中文乱码时加 --default-character-set=gbk 重跑。
6. MCP 返回 datetime 会带 Z 后缀（比库内真实值偏 8 小时），判读一律用 DATE_FORMAT(col,'%Y-%m-%d %H:%i:%s') 取字符串原值。
